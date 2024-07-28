package net.anvian.sodiumextrainformation.mixin;

import me.flashyreese.mods.sodiumextra.client.gui.SodiumExtraHud;
import net.anvian.sodiumextrainformation.SodiumExtraInformationClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mixin(SodiumExtraHud.class)
public class SodiumExtraHudMixin {
    @Final
    @Shadow
    private List<Text> textList;

    @Inject(method = "onStartTick", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void inject(MinecraftClient client, CallbackInfo ci) {
        if (SodiumExtraInformationClient.options().extraInformationSettings.showLocalTime) {
            LocalDateTime now = LocalDateTime.now();

            String timeFormat = SodiumExtraInformationClient.options().extraInformationSettings.localTimeFormat;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormat);
            String formattedNow = now.format(formatter);

            textList.add(Text.of(formattedNow));
        }

        if (SodiumExtraInformationClient.options().extraInformationSettings.showWordTime) {
            if (client.world != null) {
                long worldTime = client.world.getTimeOfDay();
                long currentDay = worldTime / 24000;
                textList.add(Text.of("Day: " + currentDay));
            }
        }

        if (SodiumExtraInformationClient.options().extraInformationSettings.showSessionTime) {
            long totalTimePlayed = SodiumExtraInformationClient.getTotalTimePlayed();
            int hours = (int) (totalTimePlayed / 3600);
            int minutes = (int) ((totalTimePlayed % 3600) / 60);
            int seconds = (int) (totalTimePlayed % 60);

            textList.add(Text.of(hours + "h " + minutes + "m " + seconds + "s"));
        }

        if (SodiumExtraInformationClient.options().extraInformationSettings.showMemoryUsage) {
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
            long maxMemory = runtime.maxMemory();

            int memoryUsagePercent = (int) ((double) usedMemory / maxMemory * 100);
            textList.add(Text.of(memoryUsagePercent + "%"));

            if (SodiumExtraInformationClient.options().extraInformationSettings.showMemoryUsageExtended) {
                long usedMemoryMB = usedMemory / (1024 * 1024);
                long maxMemoryMB = maxMemory / (1024 * 1024);
                textList.add(Text.of(usedMemoryMB + "MB / " + maxMemoryMB + "MB"));
            }
        }
    }
}
