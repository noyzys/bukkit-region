                                                # nautchkafe-region 

## Features:
- QuadTree implementation
- Multiple region shapes (circle, square, rectangle)
- Immutable region operations
- Location-based checks
- Visualizations internal api
- Region transformations
- Iteration over region points
- Edge Detection

## Example:

```java
// Selector shape:
Region region = RegionFactorySelector.createRegion(
    "circle",
    RegionLocation.fromBukkit(player.getLocation()),
    10.0
);

// rectangle 10x20
Region rectangle = RegionFactorySelector.createRegion(
    "rectangle", 
    centerLoc, 
    10.0, 20.0
);

// square 
Region square = RegionFactorySelector.createRegion(
    "square", 
    centerLoc, 
    15.0
);

// Manual creator:
RegionLocation min = new RegionLocation("world", 0, 64, 0);
RegionLocation max = new RegionLocation("world", 10, 64, 10);

Region customRegion = RegionSpawner.of(min, max,
    (x, z) -> x >= min.x() && x <= max.x() && z >= min.z() && z <= max.z()
);

double radius = 10.0;
RegionLocation center = new RegionLocation("world", 0, 64, 0);
RegionLocation circleMin = new RegionLocation("world", center.x() - radius, center.y(), center.z() - radius);
RegionLocation circleMax = new RegionLocation("world", center.x() + radius, center.y(), center.z() + radius);

Region circleRegion = RegionSpawner.of(circleMin, circleMax,
    (x, z) -> Math.pow(x - center.x(), 2) + Math.pow(z - center.z(), 2) <= Math.pow(radius, 2)
); 
// ...

```

## Visualization border material 
```java
Region spawnRegion = createCircularRegion.apply(player.getLocation());
visualize.accept(
    spawnRegion,
    player,
    Material.GLOWSTONE, // block vizualization
    200 // refresh time
);

Region customRegion = RegionFactorySelector.createRegion(
    "rectangle",
    centerLoc,
    20.0, 30.0
);

RegionVisualizer.visualize.accept(
    customRegion,
    player,
    Material.DIAMOND_BLOCK,
    100
);
```
- TIP for performance:
- Spatial Partitioning - QuadTree, Rtree, Octree (2d grid) ....
- cache BlockData,
- Batch processing, 
- Async render 

## Region#contains :: Checking Location Containment
```java
Player player = event.getPlayer();
RegionLocation playerLoc = RegionLocation.fromBukkit(player.getLocation());

if (circle.contains(playerLoc)) {
    player.sendMessage("You're inside the circle region!");
}
```

```java
boolean isInside = region.contains(RegionLocation.fromBukkit(player.getLocation()));
```
## Explanation:
- region.contains(...): This checks if the location provided is within the boundaries of the region.

## Region#applyToBounds :: Region Transformations
```java
// Scale region by 20%
Region scaled = circle.applyToBounds(x -> x * 1.2);

// Move region 5 blocks east
Region moved = circle.applyToBounds(x -> x + 5);
```

## Explanation:
- region.applyToBounds(...): This method creates a new Region where the bounds (min and max coordinates) have been modified according to the provided function.
- x -> x + 5.0: This is the function that adds 5.0 to every coordinate (x, y, z), effectively shifting the region by 5 units in each direction.
**The result is a new Region (movedRegion) with the adjusted boundaries.**

## Iterating Through Region Points
```java
// Get all locations in region with 1-block step
List<RegionLocation> allPoints = circle.forEachInRegion(1.0);

// Visualize region with particles
circle.forEachInRegion(0.5).forEach(loc -> {
    player.spawnParticle(
        Particle.REDSTONE, 
        loc.toBukkit(), 
        1, 
        new Particle.DustOptions(Color.RED, 1)
    );
});
```

## Region#center Getting Region Center
```java
RegionLocation center = circle.center();
player.teleport(center.toBukkit());
```

## Practical use case:
```java
@EventHandler
void onPlayerMove(PlayerMoveEvent event) {
    RegionLocation spawn = new RegionLocation("world", 0, 64, 0);
    Region spawnRegion = RegionFactorySelector.createRegion("square", spawn, 50.0);
    
    if (spawnRegion.contains(RegionLocation.fromBukkit(event.getTo()))) {
        event.getPlayer().sendMessage("Welcome to spawn area!");
    }
}
```

## Example for block change place block:

```java
    Function<RegionLocation, Region> createSpawn = center -> 
        RegionFactorySelector.createRegion("circle", center, 15.0);
    
    BiPredicate<Region, RegionLocation> isBorder = (region, loc) -> 
        !region.contains(new RegionLocation(loc.world(), loc.x() + 1, loc.y(), loc.z())) ||
        !region.contains(new RegionLocation(loc.world(), loc.x() - 1, loc.y(), loc.z())) ||
        !region.contains(new RegionLocation(loc.world(), loc.x(), loc.y(), loc.z() + 1)) ||
        !region.contains(new RegionLocation(loc.world(), loc.x(), loc.y(), loc.z() - 1));
    
    UnaryOperator<RegionLocation> withYOffset = loc -> 
        new RegionLocation(loc.world(), loc.x(), loc.y() + 0.5, loc.z());
    
    BiConsumer<Region, Player> visualize = (region, player) -> {
        Consumer<RegionLocation> showBorder = loc -> 
            player.sendBlockChange(
                withYOffset.apply(loc).toBukkit(),
                Material.RED_STAINED_GLASS.createBlockData()
            );
        
        region.forEachInRegion(1.0)
            .stream()
            .filter(loc -> isBorder.test(region, loc))
            .forEach(showBorder);
    };
    
    BiFunction<Region, Player, Runnable> scheduleCleanup = (region, player) -> 
        () -> region.forEachInRegion(1.0).stream()
            .map(withYOffset)
            .forEach(loc -> 
                player.sendBlockChange(
                    loc.toBukkit(), 
                    loc.toBukkit().getBlock().getBlockData()
                )
            );
```

```java
EventHandler
void onPlayerInteract(PlayerInteractEvent event) {
    renderer.render(event.getPlayer(), Particle.END_ROD);
}

// Optimized rendering version (checks distance/chunk loading)
final class RegionRenderer {
    private final Region region;
    private final double step;

    RegionRenderer(Region region, double step) {
        this.region = region;
        this.step = step;
    }

    void render(Player player, Particle particle) {
        Chunk playerChunk = player.getLocation().getChunk();
        String world = region.center().world();

        region.forEachInRegion(step).forEach(loc -> {
            Location bukkitLoc = loc.toBukkit();
            
            if (!bukkitLoc.getWorld().getName().equals(world)) {
                return;
            }

            // ???
            if (Math.abs(playerChunk.getX() - bukkitLoc.getChunk().getX()) > 5) {
                return;
            }

            player.spawnParticle(particle, bukkitLoc, 1);
        });
    }
}
```

**If you are interested in exploring functional programming and its applications within this project visit the repository at [vavr-in-action](https://github.com/noyzys/bukkit-vavr-in-action), [fp-practice](https://github.com/noyzys/fp-practice).**

