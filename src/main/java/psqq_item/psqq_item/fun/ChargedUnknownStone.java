package psqq_item.psqq_item.fun;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;

public class ChargedUnknownStone extends Item {
    public ChargedUnknownStone(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // 检查目标不是玩家
        if (!(target instanceof Player) && attacker.level() instanceof ServerLevel serverLevel) {
            // 创建一个闪电实体
            LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
            if (lightningBolt != null) {
                // 设置闪电的位置为目标实体的位置
                lightningBolt.moveTo(target.position());
                // 将闪电添加到世界中
                serverLevel.addFreshEntity(lightningBolt);
            }
        }
        // 调用父类方法以保持原有功能
        return super.hurtEnemy(stack, target, attacker);
    }
}