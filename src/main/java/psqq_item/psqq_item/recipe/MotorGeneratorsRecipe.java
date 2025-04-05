package psqq_item.psqq_item.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import psqq_item.psqq_item.ModMain;

public class MotorGeneratorsRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;
    private final int processingTime; // 处理时间字段
    private final int energyPerTick; // 添加每tick能量消耗字段

    public MotorGeneratorsRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> recipeItems,
                                 int processingTime, int energyPerTick) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
        this.processingTime = processingTime;
        this.energyPerTick = energyPerTick;
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if (pLevel.isClientSide()) {
            return false;
        }

        // 统计容器中有多少非空物品
        int nonEmptySlots = 0;
        for (int i = 0; i < 6; i++) {
            if (!pContainer.getItem(i).isEmpty()) {
                nonEmptySlots++;
            }
        }

        // 如果容器中的非空物品数量少于配方所需材料数量，直接返回false
        if (nonEmptySlots < recipeItems.size()) {
            return false;
        }

        // 检查物品是否匹配配方中的材料（无序匹配）
        boolean[] matched = new boolean[recipeItems.size()];
        int matchCount = 0;

        for (int i = 0; i < 6; i++) {
            ItemStack stack = pContainer.getItem(i);
            if (stack.isEmpty()) continue;

            for (int j = 0; j < recipeItems.size(); j++) {
                if (!matched[j] && recipeItems.get(j).test(stack)) {
                    matched[j] = true;
                    matchCount++;
                    break;
                }
            }
        }

        return matchCount == recipeItems.size();
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return recipeItems;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    // 获取处理时间的方法
    public int getProcessingTime() {
        return processingTime;
    }

    // 获取每tick能量消耗的方法
    public int getEnergyPerTick() {
        return energyPerTick;
    }

    // 定义配方类型
    public static class Type implements RecipeType<MotorGeneratorsRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "motor_generators";
    }

    // 序列化器用于从JSON加载配方
    public static class Serializer implements RecipeSerializer<MotorGeneratorsRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(ModMain.MOD_ID, "motor_generators");

        @Override
        public MotorGeneratorsRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            JsonObject outputJson = GsonHelper.getAsJsonObject(pSerializedRecipe, "output");
            ItemStack output = ShapedRecipe.itemStackFromJson(outputJson);

            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.create();

            for (int i = 0; i < ingredients.size(); i++) {
                Ingredient ingredient = Ingredient.fromJson(ingredients.get(i));
                inputs.add(ingredient);
            }

            // 从JSON中读取处理时间，默认200
            int processingTime = GsonHelper.getAsInt(pSerializedRecipe, "processing_time", 200);

            // 从JSON中读取每tick能量消耗，默认50
            int energyPerTick = GsonHelper.getAsInt(pSerializedRecipe, "energy_per_tick", 50);

            return new MotorGeneratorsRecipe(pRecipeId, output, inputs, processingTime, energyPerTick);
        }

        @Override
        public @Nullable MotorGeneratorsRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            NonNullList<Ingredient> inputs = NonNullList.create();
            int size = pBuffer.readInt();

            for (int i = 0; i < size; i++) {
                inputs.add(Ingredient.fromNetwork(pBuffer));
            }

            ItemStack output = pBuffer.readItem();
            int processingTime = pBuffer.readInt(); // 读取处理时间
            int energyPerTick = pBuffer.readInt(); // 读取每tick能量消耗

            return new MotorGeneratorsRecipe(pRecipeId, output, inputs, processingTime, energyPerTick);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, MotorGeneratorsRecipe pRecipe) {
            pBuffer.writeInt(pRecipe.recipeItems.size());
            for (Ingredient ingredient : pRecipe.recipeItems) {
                ingredient.toNetwork(pBuffer);
            }
            pBuffer.writeItem(pRecipe.output);
            pBuffer.writeInt(pRecipe.processingTime); // 写入处理时间
            pBuffer.writeInt(pRecipe.energyPerTick); // 写入每tick能量消耗
        }
    }
}