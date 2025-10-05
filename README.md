# society-trading
A data-driven trading mod

## Admin Commands

### /openshop
Opens a specific shop for a player. Requires OP level 2 (admin permissions).

**Usage:**
```
/openshop <player> <shopID>
```

**Parameters:**
- `player`: The target player to open the shop for
- `shopID`: The unique identifier of the shop to open (without the `society_trading:` namespace prefix)

**Example:**
```
/openshop Steve example_shop
```

This will open the shop with ID `society_trading:example_shop` for the player named Steve.
