package com.maxwell.cyber_ware_port.common.block.robosurgeon.surgeon;

import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.common.item.base.BodyPartType;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import java.util.ArrayList;
import java.util.List;

public class SurgeryManager {

    /**
     * 手術トランザクション：台と体の間でアイテムを安全に交換し、競合を解決する
     */
    public static void execute(ServerPlayer player, IItemHandlerModifiable table, ItemStackHandler body) {
        List<ItemStack> ejectList = new ArrayList<>();

        // 1. スロット単位の適用（台にある実体アイテムを体内へ移動）
        for (int i = 0; i < table.getSlots(); i++) {
            ItemStack tableStack = table.getStackInSlot(i);
            if (isGhost(tableStack))
                continue;

            // 体内の古いパーツを回収
            ItemStack oldPart = body.getStackInSlot(i);
            if (!oldPart.isEmpty()) {
                ejectList.add(oldPart.copy());
            }

            // 台のアイテムを体内へ（抽出：extractItem により台からは消える）
            // 抽出前にコピー取っておく（念のため）
            ItemStack insertedDeducted = table.extractItem(i, 64, false);
            body.setStackInSlot(i, insertedDeducted);
        }

        // 2. 部位(BodyPartType)ベースの物理競合・Incompatible定義による解決
        resolveConflicts(body, ejectList);

        // 3. 回収された全アイテムをプレイヤーへ安全に返却（消滅バグの完全封じ込め）
        for (ItemStack stack : ejectList) {
            if (stack.isEmpty())
                continue;
            // まずインベントリへの追加を試みる
            if (!player.getInventory().add(stack)) {
                // インベントリが一杯ならドロップさせる
                // player.drop(stack, false) は足元に落とすが、ブロック埋まりのリスクがあるため
                // 確実にエンティティ化されるよう ItemEntity を生成することも考慮できるが、
                // 通常は drop(..., true) で少し散らすか、drop(..., false) で足元。
                // ここではバニラの挙動に合わせて drop(..., false) を使用しつつ、
                // nullチェック等を含めて安全に行う。
                net.minecraft.world.entity.item.ItemEntity itemEntity = player.drop(stack, false);
                if (itemEntity != null) {
                    itemEntity.setNoPickUpDelay(); // すぐ拾えるように
                    itemEntity.setUnlimitedLifetime(); // デスポーンしにくいように（必要あれば）
                }
            }
        }
    }

    private static void resolveConflicts(ItemStackHandler body, List<ItemStack> ejectList) {
        // 重複チェック
        for (int i = 0; i < body.getSlots(); i++) {
            ItemStack s1 = body.getStackInSlot(i);
            ICyberware cw1 = CyberwareAPI.getCyberware(s1);
            if (cw1 == null)
                continue; // 空スロットまたはCyberwareでない

            for (int j = i + 1; j < body.getSlots(); j++) {
                ItemStack s2 = body.getStackInSlot(j);
                ICyberware cw2 = CyberwareAPI.getCyberware(s2);
                if (cw2 == null)
                    continue;

                boolean conflict = false;

                // 1. BodyPartType が同じ場合（物理的に同じ場所）
                // ただし、BodyPartType.NONE は競合しない（汎用アップグレードなど）
                if (cw1.getBodyPartType(s1) != BodyPartType.NONE
                        && cw1.getBodyPartType(s1) == cw2.getBodyPartType(s2)) {
                    conflict = true;
                }

                // 2. 明示的な incompatible 定義がある場合
                if (!conflict) {
                    if (cw1.isIncompatible(s1, s2) || cw2.isIncompatible(s2, s1)) {
                        conflict = true;
                    }
                }

                if (conflict) {
                    // 競合発生。品質(Quality)の低い方を強制排出
                    // Qualityが高い方が勝つ。同じなら後勝ち（あるいは実装順）だが、ここでは j (後) を排出候補とするか？
                    // CyberwareItemのQuality: 0=Human, 1=Standard, ...

                    int q1 = cw1.getQuality(s1);
                    int q2 = cw2.getQuality(s2);

                    int loserIndex;
                    if (q1 > q2) {
                        loserIndex = j;
                    } else if (q2 > q1) {
                        loserIndex = i;
                    } else {
                        // 同品質の場合。
                        // 既存実装では (q1 >= q2) ? j : i だったので、i(先)優先（jが負ける）。
                        loserIndex = j;
                    }

                    // 排出
                    ItemStack loserStack = body.getStackInSlot(loserIndex);
                    ejectList.add(loserStack.copy());
                    body.setStackInSlot(loserIndex, ItemStack.EMPTY);

                    // もし i が排出されたら、これ以上 i と他の比較はできないので inner loop 終了
                    // outer loop i の次の周回へ（ただし i は今空になったので直後の check で continue される）
                    if (loserIndex == i) {
                        break;
                    }
                    // j が排出されたら、次の j+1 へ
                }
            }
        }
    }

    public static boolean isGhost(ItemStack s) {
        return !s.isEmpty() && s.hasTag() && s.getTag().getBoolean("cyberware_ghost");
    }
}