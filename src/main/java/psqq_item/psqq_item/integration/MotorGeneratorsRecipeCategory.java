package psqq_item.psqq_item.integration;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import psqq_item.psqq_item.ModBlocks;
import psqq_item.psqq_item.ModMain;
import psqq_item.psqq_item.recipe.MotorGeneratorsRecipe;

public class MotorGeneratorsRecipeCategory implements IRecipeCategory<MotorGeneratorsRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(ModMain.MOD_ID, "motor_generators");
    public static final ResourceLocation TEXTURE =
            new ResourceLocation(ModMain.MOD_ID, "textures/gui/motor_generators_gui_jei.png");

    private final IDrawable background;
    private final IDrawable icon;

    public MotorGeneratorsRecipeCategory(IGuiHelper helper) {
        // 这里使用GUI贴图，调整坐标和尺寸以显示合适的区域
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.MOTOR_GENERATORS.get()));
    }

    @Override
    public RecipeType<MotorGeneratorsRecipe> getRecipeType() {
        return JEIPlugin.MOTOR_GENERATORS_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.psqq_item.motor_generators");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MotorGeneratorsRecipe recipe, IFocusGroup focuses) {
        // 添加输入物品
        int inputSlotIndex = 0;
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            // 根据UI布局调整输入物品的位置
            builder.addSlot(RecipeIngredientRole.INPUT, 18 + (i % 3) * 18, 18 + (i / 3) * 18)
                    .addIngredients(recipe.getIngredients().get(i));
            inputSlotIndex++;
        }

        // 添加输出物品
        builder.addSlot(RecipeIngredientRole.OUTPUT, 112, 27)
                .addItemStack(recipe.getResultItem(null));
    }
}
