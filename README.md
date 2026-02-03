🛠️ Data Pack Guide: Creating Custom Cyberware
This guide explains how to add new Cyberware to the mod using only Data Packs (JSON). This system allows you to turn any existing item (from Vanilla or other mods) into functional Cyberware with custom stats and attributes without writing any Java code.

📂 1. Directory Structure
Files must be placed in your data pack using the following structure:

code
Text
your_data_pack/
 ┗ data/
    ┗ [your_namespace]/
       ┗ cyberware/
          ┗ [item_name].json
Example: data/my_addon/cyberware/titanium_bones.json

📝 2. Basic Cyberware Template
Create a JSON file to define the properties of the item.

code
JSON
{
  "item": "minecraft:iron_ingot",
  "slot": "BONES",
  "essence": 20,
  "max_install": 1,
  "attributes": [
    {
      "attribute": "minecraft:generic.max_health",
      "amount": 4.0,
      "operation": "ADDITION"
    }
  ]
}
Parameter Reference
Key	Type	Description
item	String	The Registry ID of the item to be converted.
slot	String	Body slot: EYES, BRAIN, HEART, LUNGS, STOMACH, SKIN, MUSCLE, BONES, ARMS, HANDS, LEGS, BOOTS.
essence	Integer	Cost to install (Human max is 100).
max_install	Integer	Max count allowed in that specific slot (e.g., 2 for eyes).
attributes	Array	List of attribute modifiers applied while installed.
⚙️ 3. Attribute Modifiers
You can modify various player stats using the attributes list.

Attribute: The ID of the attribute (e.g., minecraft:generic.movement_speed, minecraft:generic.attack_damage).
Amount: The value to change.
Operation:
ADDITION: Adds the amount to the base value.
MULTIPLY_BASE: Multiplies the base value.
MULTIPLY_TOTAL: Multiplies the final value including other modifiers.
🔗 4. Requirements & Compatibility
You can set installation rules to prevent certain items from being used together or to require "base" parts.

code
JSON
{
  "item": "minecraft:netherite_ingot",
  "slot": "SKIN",
  "essence": 40,
  "prerequisites": [
    "minecraft:iron_ingot"
  ],
  "incompatible": [
    "minecraft:leather"
  ]
}
Rule Details
prerequisites: A list of Item IDs that must be installed in the body before this part can be added.
incompatible: A list of Item IDs that cannot be installed at the same time as this part.
💬 5. Tooltips (Localisation)
The mod automatically generates tooltips based on the item ID. To add a custom description, add a line to your resource pack's lang file (e.g., en_us.json):

code
JSON
{
  "cyberware.tooltip.iron_ingot": "Subdermal iron plating that increases the user's durability."
}
Note: The key format must be cyberware.tooltip.[item_path].

🚀 6. Implementation Tips
Live Reload: Use the /reload command in-game to apply changes to your JSON files instantly.
Exclusivity: Parts in the same slot with max_install: 1 will effectively replace each other during surgery.
