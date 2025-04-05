package psqq_item.psqq_item.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import psqq_item.psqq_item.ModMain;
import psqq_item.psqq_item.recipe.MotorGeneratorsRecipe;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    public static RecipeType<MotorGeneratorsRecipe> MOTOR_GENERATORS_TYPE =
            new RecipeType<>(new ResourceLocation(ModMain.MOD_ID, "motor_generators"),
                    MotorGeneratorsRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ModMain.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new
                MotorGeneratorsRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

        List<MotorGeneratorsRecipe> recipes = recipeManager.getAllRecipesFor(MotorGeneratorsRecipe.Type.INSTANCE);
        registration.addRecipes(MOTOR_GENERATORS_TYPE, recipes);
    }
}
