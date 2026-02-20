# 🛠️ Data Pack Guide: Creating Custom Cyberware & Cybermob spawning

This guide provides the complete specification for adding Cyberware to the mod via **Data Packs (JSON)**. This system allows you to turn any item into a functional implant with full support for attributes, energy management, and surgery rules.

---

## 📂 1. Directory Structure
Files must be placed in your data pack using the following structure:

```text
your_data_pack/
 ┗ data/
    ┗ [your_namespace]/
       ┗ cyberware/
          ┗ [item_name].json
```

---

## 📝 2. Full Cyberware Template
Here is a comprehensive JSON template containing all supported properties.

```json
{
  "item": "minecraft:netherite_ingot",
  "slot": "BONES",
  "essence": 40,
  "max_install": 1,
  "quality": 1,
  "body_part": "NONE",
  "attributes": [
    {
      "attribute": "minecraft:generic.max_health",
      "amount": 4.0,
      "operation": "ADDITION"
    }
  ],
  "energy": {
    "consumption": 10,
    "generation": 0,
    "storage": 1000,
    "stacking": "LINEAR"
  },
  "prerequisites": [
    "minecraft:iron_ingot"
  ],
  "incompatible": [
    "minecraft:leather"
  ]
}
```

---

## ⚙️ 3. Core Properties

| Key | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `item` | String | (Required) | Registry ID of the item (e.g., `minecraft:iron_ingot`). |
| `slot` | String | (Required) | The slot category: `EYES`, `BRAIN`, `HEART`, `LUNGS`, `STOMACH`, `SKIN`, `MUSCLE`, `BONES`, `ARMS`, `HANDS`, `LEGS`, `BOOTS`. |
| `essence` | Integer | `20` | Cost to install (Human max: 100). |
| `max_install`| Integer | `1` | Maximum number of this specific item allowed in the target slot. |
| `quality` | Integer | `1` | Installation priority. `0` = Organic, `1` = Standard, `2+` = High-tier. |
| `body_part` | String | `NONE` | Physical limb assignment (e.g., `ARM_LEFT`). See Section 4 for details. |

---

## 🧠 4. Advanced Logic: Quality & Body Part
These two fields control how the Robosurgeon manages conflicts during surgery.

### The Quality System (`quality`)
Quality determines which part "wins" if they occupy the same space.
- **Priority Override:** If you try to install a part into a slot already occupied by a different item with the same `body_part`, the Robosurgeon compares their quality.
- **Upgrading:** If the new part has a **higher** quality, the old part is automatically marked for uninstallation. If it's lower or equal, the installation is rejected.

### Body Part Specificity (`body_part`)
While `slot` defines the general area (e.g., `ARMS`), `body_part` defines the **physical limb**.
- **Limb Exclusivity:** Setting a part to `ARM_LEFT` ensures it only replaces the left arm. You cannot install two different items that both claim to be the `ARM_LEFT`.
- **Valid Values:** `NONE`, `EYES`, `BRAIN`, `HEART`, `LUNGS`, `STOMACH`, `SKIN`, `MUSCLE`, `BONES`, `ARM_LEFT`, `ARM_RIGHT`, `HAND`, `LEG_LEFT`, `LEG_RIGHT`, `FOOT`.

---

## ⚡ 5. Energy Management
The `energy` object defines how the part interacts with the internal power grid (FE).

- **`consumption`**: FE consumed per tick.
- **`generation`**: FE generated per tick (e.g., a generator).
- **`storage`**: Internal battery capacity provided by this part.
- **`stacking`**: Logic for combining costs of multiple identical items:
  - `LINEAR`: Cost is multiplied by the number of items.
  - `DIMINISHING`: Cost increases less with each additional item.
  - `STATIC`: Cost remains the same regardless of how many are installed.

---

## 🛡️ 6. Attribute Modifiers
Apply standard Minecraft attributes while the part is installed.

- **`attribute`**: The ID of the attribute (e.g., `minecraft:generic.movement_speed`).
- **`amount`**: The numerical value to apply.
- **`operation`**:
  - `ADDITION`: Adds the amount to the base value.
  - `MULTIPLY_BASE`: Multiplies the base value by (1 + amount).
  - `MULTIPLY_TOTAL`: Multiplies the final value by (1 + amount).

---

## 🔗 7. Installation Rules
Manage dependencies and conflicts between different items.

- **`prerequisites`**: A list of Item IDs that **must already be installed** in the body before this part can be added.
- **`incompatible`**: A list of Item IDs that **cannot coexist** with this part. If one is present, the other cannot be installed.

---

## 💬 8. Localisation (Tooltips)
Custom items automatically search for a tooltip key in your resource pack's `lang/en_us.json`. The mod uses the item's registry path to form the key.

**Format:**
```json
{
  "cyberware.tooltip.[item_path]": "Your custom description here."
}
```

**Example for `minecraft:netherite_ingot`:**
```json
{
  "cyberware.tooltip.netherite_ingot": "A reinforced bone plating made of ancient debris."
}
```

---

## 🚀 9. Implementation Tips
- **Surgery Simulation:** Use the Robosurgeon GUI to see how "Ghost Items" (current implants) interact with your new JSON-defined parts.
- **Pristine State:** By default, all Cyberware added via Data Packs is considered "Pristine" (Manufactured). If an item is "Scavenged" (Damaged), its energy costs are doubled and attribute bonuses are halved.
- **Live Reload:** You can use the `/reload` command in-game to apply changes to your JSON files without restarting the game.

## 10. Modifying Mobs with Json
Files must be placed in your data pack using the following structure:

```text
your_data_pack/
 ┗ data/
    ┗ [your_namespace]/
       ┗ cyberware/
          ┗ mobs
　　　　　　　┗ [mob_name].json
          
```
This is the json that converts a wither skeleton to a cyberwither skeleton.
**Format:**
```json
{
    "mob": "minecraft:wither_skeleton",
    "replace_with": "cyber_ware_port:cyber_wither_skeleton",
    "chance": 0.2,
    "is_high_tier": true,
    "special_drops": [
        "cyber_ware_port:internal_defibrillator",
        "cyber_ware_port:rapid_fire_flywheel"
    ],
    "forbidden_drops": [
        "minecraft:stone_sword"
    ]
}
```
The function of each array is as follows:
| Key              | Type            | Default   | Description                                                                                        |
| :--------------- | :-------------- | :-------- | :--------------------------------------------------------------------------------------------------|
| `mob`            | String          | (Required)| Registry ID of the vanilla mob to be replaced (e.g., `minecraft:zombie`)                           |
| `replace_with`   | String          | (Required)| The registry ID of the target Cybermob (e.g., `cyber_ware_port:cyber_wither_skeleton`)             |
| `chance`         | Double (0.0~1.0)| `0.0`     | Basic probability of substitution occurring (0.15 = 15%)                                           |
| `is_high_tier`   | Boolean         | `false`   | Treat them as high-tier mobs (adding powerful cyberware to the drop pool)                          |
| `special_drops`  | Array of String | `[]`      | Unique drop item registry ID list (duplicates are added to the pool, making it easier to get them) |
| `forbidden_drops`| Array of String | `[]`      | Registry ID list of no-drop items (to be excluded from the pool)                                   |

