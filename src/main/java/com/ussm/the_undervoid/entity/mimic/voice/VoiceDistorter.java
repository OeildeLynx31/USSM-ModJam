package com.ussm.the_undervoid.entity.mimic.voice;

import java.util.function.Supplier;

// voix du mimic quand il est revelé : plus grave, qui tremble, hachée et crade
public class VoiceDistorter {
    // pas l'OpusDecoder/Encoder de voicechat direct ici, sinon ca crash quand le mod est pas installé
    public interface Codec {
        short[] decode(byte[] opus);

        byte[] encode(short[] pcm);

        void close();
    }

    private static final int FRAME = 960; // 20ms a 48kHz
    private static final double SAMPLE_RATE = 48000;

    private final Codec codec;
    private float[] buf = new float[8192];
    private int len;
    private double pos;
    private long t;
    private boolean closed;

    public VoiceDistorter(Codec codec) {
        this.codec = codec;
    }

    // renvoie la prochaine frame opus, null quand c'est fini
    public synchronized byte[] next(Supplier<byte[]> source) {
        if (closed) return null;

        boolean ended = false;
        int need = (int) (pos + FRAME * 0.85) + 2;
        while (len < need) {
            byte[] frame = source.get();
            if (frame == null) {
                ended = true;
                break;
            }
            short[] pcm = codec.decode(frame);
            if (pcm == null) continue;
            if (len + pcm.length > buf.length) buf = java.util.Arrays.copyOf(buf, (len + pcm.length) * 2);
            for (short s : pcm) buf[len++] = s / 32768f;
        }
        if (ended && pos >= len - 1) return null;

        short[] out = new short[FRAME];
        for (int i = 0; i < FRAME; i++, t++) {
            int idx = (int) pos;
            float s = 0f;
            if (idx + 1 < len) {
                float frac = (float) (pos - idx);
                s = buf[idx] * (1 - frac) + buf[idx + 1] * frac;
            }
            // plus grave, avec une hauteur qui flotte
            pos += 0.7 + 0.04 * Math.sin(2 * Math.PI * t / (SAMPLE_RATE * 0.5));
            // voix qui tremble un peu
            s *= 0.8 + 0.3 * Math.sin(2 * Math.PI * 30 * t / SAMPLE_RATE);
            s = Math.round(s * 400) / 400f;
            s = (float) Math.tanh(s * 1.3);
            out[i] = (short) (s * 32767);
        }

        int shift = Math.min((int) pos, len);
        if (shift > 0) {
            System.arraycopy(buf, shift, buf, 0, len - shift);
            len -= shift;
            pos -= shift;
        }
        return codec.encode(out);
    }

    public synchronized void close() {
        if (closed) return;
        closed = true;
        codec.close();
    }
}
