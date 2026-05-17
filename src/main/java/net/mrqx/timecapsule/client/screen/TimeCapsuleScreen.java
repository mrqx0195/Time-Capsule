package net.mrqx.timecapsule.client.screen;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.mrqx.timecapsule.TimeCapsule;
import net.mrqx.timecapsule.item.TimeMode;
import net.mrqx.timecapsule.menu.TimeCapsuleMenu;
import net.mrqx.timecapsule.network.TimeCapsuleSealPayload;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import javax.annotation.Nullable;

public class TimeCapsuleScreen extends AbstractContainerScreen<TimeCapsuleMenu> {
    public static final Identifier BACKGROUND_TEXTURE = TimeCapsule.prefix("textures/gui/time_capsule.png");
    private static final Gson GSON = new Gson();
    
    public int years;
    public int months;
    public int days;
    public int hours;
    public int minutes;
    public int seconds;
    public TimeMode timeMode = TimeMode.REAL_TIME;
    @Nullable
    public EditBox messageInput;
    
    public TimeCapsuleScreen(TimeCapsuleMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 244, 235);
        this.inventoryLabelX = 42;
    }
    
    @Override
    protected void init() {
        super.init();
        
        this.messageInput = new EditBox(
            this.font,
            this.leftPos + 14,
            this.topPos + 19,
            214,
            12,
            Component.translatable("gui.time_capsule.message")
        );
        this.messageInput.setTextColor(-1);
        this.messageInput.setTextColorUneditable(-1);
        this.messageInput.setInvertHighlightedTextColor(false);
        this.messageInput.setBordered(false);
        this.messageInput.setMaxLength(32767);
        this.messageInput.setValue("");
        this.addRenderableWidget(this.messageInput);
        
        this.addRenderableWidget(Button.builder(Component.translatable("gui.time_capsule.mode_toggle", Component.translatable(timeModeKey(this.timeMode))), this::onModeToggle)
            .bounds(this.leftPos + 10, this.topPos + 60, 70, 20).build());
        
        this.addRenderableWidget(Button.builder(Component.translatable("gui.time_capsule.seal"), this::onSeal)
            .bounds(this.leftPos + 10, this.topPos + 90, 70, 20).build());
        
        int timeRowY = this.topPos + 43;
        int minusX = this.leftPos + 85;
        int plusX = minusX + 47;
        int i = 15;
        
        this.createAdjustButton(minusX, timeRowY, "-", _ -> this.adjustYear(-1));
        this.createAdjustButton(plusX, timeRowY, "+", _ -> this.adjustYear(1));
        timeRowY += i;
        
        this.createAdjustButton(minusX, timeRowY, "-", _ -> this.adjustMonth(-1));
        this.createAdjustButton(plusX, timeRowY, "+", _ -> this.adjustMonth(1));
        timeRowY += i;
        
        this.createAdjustButton(minusX, timeRowY, "-", _ -> this.adjustDay(-1));
        this.createAdjustButton(plusX, timeRowY, "+", _ -> this.adjustDay(1));
        timeRowY += i;
        
        this.createAdjustButton(minusX, timeRowY, "-", _ -> this.adjustHour(-1));
        this.createAdjustButton(plusX, timeRowY, "+", _ -> this.adjustHour(1));
        timeRowY += i;
        
        this.createAdjustButton(minusX, timeRowY, "-", _ -> this.adjustMinute(-1));
        this.createAdjustButton(plusX, timeRowY, "+", _ -> this.adjustMinute(1));
        timeRowY += i;
        
        this.createAdjustButton(minusX, timeRowY, "-", _ -> this.adjustSecond(-1));
        this.createAdjustButton(plusX, timeRowY, "+", _ -> this.adjustSecond(1));
    }
    
    private void createAdjustButton(int x, int y, String label, Button.OnPress action) {
        this.addRenderableWidget(Button.builder(Component.literal(label), action).bounds(x, y, 10, 10).build());
    }
    
    @Override
    protected void setInitialFocus() {
        if (this.messageInput != null) {
            this.setInitialFocus(this.messageInput);
        }
    }
    
    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.isEscape()) {
            if (this.minecraft.player != null) {
                this.minecraft.player.closeContainer();
            }
            return true;
        } else if (this.messageInput != null) {
            return this.messageInput.keyPressed(event) || this.messageInput.canConsumeInput() || super.keyPressed(event);
        }
        return super.keyPressed(event);
    }
    
    private void onModeToggle(Button button) {
        this.timeMode = switch (this.timeMode) {
            case GAME_TICK -> TimeMode.REAL_TIME;
            case REAL_TIME -> TimeMode.GAME_TICK;
        };
        button.setMessage(Component.translatable("gui.time_capsule.mode_toggle", Component.translatable(timeModeKey(this.timeMode))));
    }
    
    private void adjustYear(int delta) {
        this.years = Math.max(0, this.years + delta);
    }
    
    private void adjustMonth(int delta) {
        this.months = Math.max(0, this.months + delta);
    }
    
    private void adjustDay(int delta) {
        this.days = Math.max(0, this.days + delta);
    }
    
    private void adjustHour(int delta) {
        this.hours = Math.max(0, this.hours + delta);
    }
    
    private void adjustMinute(int delta) {
        this.minutes = Math.max(0, this.minutes + delta);
    }
    
    private void adjustSecond(int delta) {
        this.seconds = Math.max(0, this.seconds + delta);
    }
    
    private void onSeal(Button button) {
        Component message = Component.empty();
        if (this.messageInput != null) {
            String inputValue = this.messageInput.getValue();
            try {
                JsonElement json = GSON.fromJson(inputValue, JsonElement.class);
                DataResult<Pair<Component, JsonElement>> result = ComponentSerialization.CODEC.decode(JsonOps.INSTANCE, json);
                message = result.getOrThrow().getFirst();
            } catch (Exception ignored) {
                message = Component.literal(inputValue);
            }
        }
        ClientPacketDistributor.sendToServer(new TimeCapsuleSealPayload(
            message, this.timeMode,
            this.years, this.months, this.days,
            this.hours, this.minutes, this.seconds
        ));
    }
    
    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TimeCapsuleScreen.BACKGROUND_TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }
    
    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        super.extractLabels(graphics, xm, ym);
        int labelX = this.titleLabelX + 105;
        int labelY = this.titleLabelY + 38;
        int i = 15;
        Font font = this.font;
        
        MutableComponent yearComponent = Component.translatable("gui.time_capsule.year", this.years);
        graphics.text(font, yearComponent, labelX - font.width(yearComponent) / 2, labelY, 0xFF404040, false);
        labelY += i;
        
        MutableComponent monthComponent = Component.translatable("gui.time_capsule.month", this.months);
        graphics.text(font, monthComponent, labelX - font.width(monthComponent) / 2, labelY, 0xFF404040, false);
        labelY += i;
        
        MutableComponent dayComponent = Component.translatable("gui.time_capsule.day", this.days);
        graphics.text(font, dayComponent, labelX - font.width(dayComponent) / 2, labelY, 0xFF404040, false);
        labelY += i;
        
        MutableComponent hourComponent = Component.translatable("gui.time_capsule.hour", this.hours);
        graphics.text(font, hourComponent, labelX - font.width(hourComponent) / 2, labelY, 0xFF404040, false);
        labelY += i;
        
        MutableComponent minuteComponent = Component.translatable("gui.time_capsule.minute", this.minutes);
        graphics.text(font, minuteComponent, labelX - font.width(minuteComponent) / 2, labelY, 0xFF404040, false);
        labelY += i;
        
        MutableComponent secondComponent = Component.translatable("gui.time_capsule.second", this.seconds);
        graphics.text(font, secondComponent, labelX - font.width(secondComponent) / 2, labelY, 0xFF404040, false);
    }
    
    public static String timeModeKey(TimeMode timeMode) {
        return switch (timeMode) {
            case GAME_TICK -> "gui.time_capsule.mode.game_tick";
            case REAL_TIME -> "gui.time_capsule.mode.real_time";
        };
    }
}
