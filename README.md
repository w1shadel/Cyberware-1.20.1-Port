# 🛠️ Data Pack Guide: Creating Custom Cyberware (Full Version)

This guide provides the complete specification for adding Cyberware to the mod via **Data Packs (JSON)**. This system allows you to turn any item into a functional implant with full support for attributes, energy management, and surgery rules.

---

## 📂 1. Directory Structure
Files must be placed in your data pack using the following structure:

```file
your_data_pack/
 ┗ data/
    ┗ [your_namespace]/
       ┗ cyberware/
          ┗ [item_name].json
```
# 📝 2. Full Cyberware Template
Here is a comprehensive JSON template containing all supported properties.


```JSON
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
# ⚙️ 3. Core Properties
Key	Type	Default	Description
item	String	(Required)	Registry ID of the item (e.g., minecraft:iron_ingot).
slot	String	(Required)	EYES, BRAIN, HEART, LUNGS, STOMACH, SKIN, MUSCLE, BONES, ARMS, HANDS, LEGS, BOOTS.
essence	Integer	20	Cost to install (Human max: 100).
max_install	Integer	1	Max count allowed in that specific slot.
quality	Integer	1	0 = Human part, 1 = Standard, 2+ = High-tier.
body_part	String	NONE	Used for limbs: ARM_LEFT, ARM_RIGHT, LEG_LEFT, LEG_RIGHT.
# ⚡ 4. Energy Management
The energy object defines how the part interacts with the internal power grid (FE).

consumption: FE consumed per tick.
generation: FE generated per tick.
storage: Internal battery capacity provided by this part.
stacking: How costs combine when multiple are installed.
LINEAR: Cost * Count.
DIMINISHING: Reduced cost for duplicates.
STATIC: Fixed cost regardless of count.
# 🛡️ 5. Attribute Modifiers
Apply standard Minecraft attributes while the part is installed.

attribute: The ID (e.g., minecraft:generic.movement_speed).
amount: The numerical value.
operation:
ADDITION: Adds to base.
MULTIPLY_BASE: Multiplies base value.
MULTIPLY_TOTAL: Multiplies final value.
# 🔗 6. Installation Rules
Manage dependencies and conflicts.

prerequisites: A list of Item IDs that must be installed before this part.
incompatible: A list of Item IDs that cannot be installed alongside this part.
# 💬 7. Localisation (Tooltips)
Custom items automatically search for a tooltip key in your resource pack's lang/en_us.json:


```JSON
{
  "cyberware.tooltip.[item_path]": "Your custom description here."
}
```
Example: cyberware.tooltip.netherite_ingot
