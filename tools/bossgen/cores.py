"""Die zehn Boss-Kerne als 3D-Items (Mitte 0/0/0, etwa 10 Einheiten gross)."""
import sys
from paint import mat
from weapongen import E, build, sheet, write
import weapons  # Materialien w_*

def cube(cx, cy, cz, w, h, d, m):
    return E(cx - w / 2, cy - h / 2, cz - d / 2, cx + w / 2, cy + h / 2, cz + d / 2, m)

mat('c_red_glow', 'glow', (200, 30, 20), (255, 200, 170))
mat('c_lava', 'crust', (50, 30, 28), glow_col=(255, 120, 30))
mat('c_pearl', 'glow', (200, 230, 240), (255, 255, 255))
mat('c_shell', 'scale', (40, 70, 100), (100, 150, 170))
mat('c_matrix', 'crystal', (160, 220, 60), (230, 255, 160))
mat('c_flesh', 'flesh', (150, 60, 50), (220, 160, 90))

CORES = {
    'sentinel_core': [cube(0, 0, 0, 6, 6, 6, 'c_red_glow'), cube(0, 0, 0, 8, 2, 8, 'w_metal'), cube(0, 4, 0, 4, 2, 4, 'w_dmetal'), cube(0, -4, 0, 4, 2, 4, 'w_dmetal'),
                      cube(3.6, 0, 3.6, 1.2, 9, 1.2, 'w_dmetal'), cube(-3.6, 0, 3.6, 1.2, 9, 1.2, 'w_dmetal'), cube(3.6, 0, -3.6, 1.2, 9, 1.2, 'w_dmetal'),
                      cube(-3.6, 0, -3.6, 1.2, 9, 1.2, 'w_dmetal'), cube(0, 1.2, 0, 8.4, 0.8, 8.4, 'w_hazard')],
    'cryo_heart': [cube(0, 0, 0, 6, 8, 6, 'w_ice'), cube(0, 0, 0, 3, 4, 3, 'w_mint_glow'), cube(2.5, 2.5, 0, 3, 3, 3, 'w_ice'), cube(-2.5, -2, 1, 2.5, 3, 2.5, 'w_ice'),
                   cube(0, 5, 0, 2, 3, 2, 'w_ice'), cube(0, -5, 0, 1.6, 2, 1.6, 'w_ice')],
    'titan_core': [cube(0, 0, 0, 7, 7, 7, 'w_rust'), cube(0, 0, 0, 9, 3, 3, 'w_rust_blade'), cube(0, 0, 0, 3, 9, 3, 'w_rust_blade'), cube(0, 0, 0, 3, 3, 9, 'w_rust_blade'),
                   cube(0, 0, -3.8, 2.6, 2.6, 1, 'w_amber_glow'), cube(0, 0, 3.8, 2.6, 2.6, 1, 'w_amber_glow'), cube(3.8, 0, 0, 1, 2.6, 2.6, 'w_amber_glow')],
    'monolith_shard': [cube(0, -1, 0, 4, 10, 3, 'w_rune'), cube(0, 4.8, 0, 2.4, 2, 2, 'w_obsidian'), cube(0, 6, 0, 1, 1.4, 1, 'w_obsidian'),
                       cube(2.4, -3, 0, 1.4, 4, 2, 'w_obsidian'), cube(-2.2, 1, 0, 1, 3, 1.6, 'w_obsidian')],
    'graphite_heart': [cube(0, 0, 0, 7, 7, 7, 'w_graphite'), cube(-1.8, 1, -1.8, 1.4, 10, 1.4, 'w_green_glow'), cube(1.8, 0, 1.8, 1.4, 9, 1.4, 'w_green_glow'),
                       cube(1.8, -0.5, -1.8, 1.4, 8.4, 1.4, 'w_dmetal'), cube(-1.8, 0.5, 1.8, 1.4, 8.6, 1.4, 'w_dmetal'), cube(0, 3.8, 0, 7.4, 0.8, 7.4, 'w_hazard')],
    'molten_heart': [cube(0, 0, 0, 8, 8, 8, 'c_lava'), cube(0, 0, 0, 9, 4, 4, 'c_lava'), cube(0, 0, 0, 4, 9, 4, 'c_lava'), cube(0, 0, 0, 4, 4, 9, 'c_lava'),
                     cube(0, 0, -4.7, 3, 3, 0.6, 'w_flare'), cube(0, 4.7, 0, 3, 0.6, 3, 'w_flare')],
    'schrabidic_crystal': [cube(0, 0, 0, 3, 10, 3, 'w_cyan_crystal'), cube(2.4, -1.5, 0.5, 2, 6, 2, 'w_cyan_crystal'), cube(-2.2, -2, -0.6, 2, 5, 2, 'w_cyan_crystal'),
                           cube(0.6, -2.5, 2.2, 1.6, 4, 1.6, 'w_cyan_crystal'), cube(0, -4.8, 0, 7, 1.2, 6, 'w_dmetal'), cube(0, 0, 0, 1.4, 6, 1.4, 'w_venom_glow')],
    'leviathan_pearl': [cube(0, 1, 0, 5, 5, 5, 'c_pearl'), cube(0, -2.4, 0, 9, 2, 8, 'c_shell'), cube(0, 2, 3.6, 8, 5, 1, 'c_shell'),
                        cube(3.8, -0.5, 0, 1, 3, 7, 'c_shell'), cube(-3.8, -0.5, 0, 1, 3, 7, 'c_shell')],
    'polymer_matrix': [cube(0, 0, 0, 8, 1, 1, 'w_pvc'), cube(0, 0, 0, 1, 8, 1, 'w_pvc'), cube(0, 0, 0, 1, 1, 8, 'w_pvc'),
                       cube(0, 0, 0, 3.4, 3.4, 3.4, 'w_lime_glow')] + [cube(x * 3.5, y * 3.5, z * 3.5, 1.8, 1.8, 1.8, 'w_gpipe') for x in (-1, 1) for y in (-1, 1) for z in (-1, 1)],
    'queen_heart': [cube(0, 0, 0, 7, 8, 6, 'c_flesh'), cube(0, 4.6, 0, 9, 1.6, 8, 'w_cap'), cube(0, 3.6, 0, 7, 0.8, 6, 'w_gill'),
                    cube(2.6, -2, -2.6, 2, 2, 2, 'w_spore_glow'), cube(-2.6, 1, -2.6, 1.6, 1.6, 1.6, 'w_spore_glow'), cube(0, -5, 0, 2, 2, 2, 'w_hyphae')],
}

if __name__ == '__main__':
    only = [a for a in sys.argv[1:] if not a.startswith('--')] or list(CORES)
    for name in only:
        model, tex, glow = build(name, CORES[name], size=64, tilt=False, gui=0.95)
        sheet(name, model, tex, glow)
        if '--write' in sys.argv: write(name, model, tex, glow, sub='core')
    print('ok')
