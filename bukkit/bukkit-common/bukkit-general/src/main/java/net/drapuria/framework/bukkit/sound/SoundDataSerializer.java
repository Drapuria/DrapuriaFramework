/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.bukkit.sound;

import net.drapuria.framework.ObjectSerializer;
import net.drapuria.framework.beans.annotation.Component;
import org.bukkit.Sound;

@Component
public class SoundDataSerializer implements ObjectSerializer<SoundData, String> {
    @Override
    public String serialize(SoundData input) {
        return input.getSound().name() + ":" + input.getVolume() + ":" + input.getPitch();
    }

    @Override
    public SoundData deserialize(String output) {
        final String[] split = output.split(":");
        String name = split[0];
        float volume = Float.parseFloat(split[1]);
        float pitch = Float.parseFloat(split[2]);
        return new SoundData(Sound.valueOf(name), volume, pitch);
    }

    @Override
    public Class<SoundData> inputClass() {
        return SoundData.class;
    }

    @Override
    public Class<String> outputClass() {
        return String.class;
    }
}
