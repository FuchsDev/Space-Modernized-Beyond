"""Die zehn Planetenwaffen als 3D-Modelle."""
import sys
from paint import mat
from weapongen import box, build, sheet, write

mat('w_metal', 'plate', (176, 182, 192), (230, 234, 240))
mat('w_dmetal', 'frame', (62, 64, 72))
mat('w_brass', 'plate', (200, 150, 60), (250, 210, 120))
mat('w_rubber', 'fiber', (40, 40, 44))
mat('w_hazard', 'hazard', (230, 190, 30))
mat('w_blue_glow', 'glow', (40, 120, 255), (200, 240, 255))
mat('w_wrap', 'cloth', (70, 110, 120), (40, 70, 80))
mat('w_ice', 'ice', (200, 250, 240), (110, 200, 190))
mat('w_mint_glow', 'glow', (60, 220, 180), (220, 255, 245))
mat('w_rust', 'rust', (120, 70, 50), (190, 100, 50))
mat('w_rust_blade', 'rust', (160, 150, 140), (180, 90, 40))
mat('w_cloth', 'cloth', (150, 110, 70), (100, 70, 40))
mat('w_amber_glow', 'glow', (230, 120, 20), (255, 230, 150))
mat('w_obsidian', 'obsidian', (26, 18, 36), (90, 60, 130))
mat('w_rune', 'runes', (30, 22, 42), glow_col=(210, 120, 255))
mat('w_graphite', 'graphite', (58, 60, 62))
mat('w_green_glow', 'glow', (60, 200, 30), (210, 255, 150))
mat('w_flare', 'glow', (255, 90, 10), (255, 250, 210))
mat('w_scale', 'scale', (36, 110, 110), (110, 220, 210))
mat('w_bone', 'bone', (232, 226, 205))
mat('w_cyan_crystal', 'crystal', (40, 160, 170), (140, 255, 250))
mat('w_venom_glow', 'glow', (20, 200, 190), (180, 255, 250))
mat('w_prismarine', 'scale', (40, 90, 100), (90, 170, 160))
mat('w_steel', 'plate', (150, 170, 180), (220, 235, 240))
mat('w_rope', 'fiber', (160, 130, 90))
mat('w_cyan_glow', 'glow', (40, 200, 255), (220, 255, 255))
mat('w_gpipe', 'pipe', (90, 160, 50), (60, 110, 30))
mat('w_pvc', 'pvc', (226, 228, 220))
mat('w_lime_glow', 'glow', (150, 230, 20), (240, 255, 170))
mat('w_hyphae', 'fiber', (196, 182, 156))
mat('w_cap', 'cap', (150, 50, 40), (236, 222, 200))
mat('w_gill', 'gill', (230, 200, 160), (200, 170, 130), glow_col=(255, 190, 80))
mat('w_spore_glow', 'glow', (230, 150, 30), (255, 240, 170))

W = {}
W['shock_baton'] = (dict(), [
    box(0, -12, -10.6, 3.4, 3.4, 'w_dmetal'), box(0, -10.6, -4, 2.4, 2.4, 'w_rubber'), box(0, -9.4, -8.6, 3, 3, 'w_dmetal'), box(0, -6.8, -6, 3, 3, 'w_dmetal'),
    box(0, -4, -2.4, 5, 3.6, 'w_metal'), box(0, -2.4, 6, 2, 2, 'w_metal'), box(0, -0.6, 0.8, 2.4, 2.4, 'w_hazard'), box(0, 3, 4.2, 2.4, 2.4, 'w_hazard'),
    box(0, 6, 7.4, 3.4, 3.4, 'w_dmetal'), box(0, 7.4, 8.4, 2.8, 2.8, 'w_blue_glow'), box(0, 8.4, 9.8, 3.4, 3.4, 'w_dmetal'),
    box(0, 9.8, 10.8, 2.8, 2.8, 'w_blue_glow'), box(0, 10.8, 12.4, 1.6, 1.6, 'w_blue_glow'),
    box(0, 10.8, 13, 0.8, 0.8, 'w_metal', ox=1.8), box(0, 10.8, 13, 0.8, 0.8, 'w_metal', ox=-1.8),
    box(0, -2, 2.4, 1.4, 2, 'w_hazard', ox=1.8)])
W['frostbite_cleaver'] = (dict(), [
    box(0, -12.4, -11, 2.8, 2.8, 'w_mint_glow'), box(0, -11, -3, 2, 2, 'w_wrap'), box(0, -3, -1.8, 6, 3, 'w_metal'),
    box(1.0, -1.8, 9.5, 7, 1, 'w_ice'), box(-2.9, -1.8, 9.5, 1.2, 1.8, 'w_metal'), box(4.75, -1, 9, 0.5, 0.8, 'w_mint_glow'),
    box(1.2, 9.5, 11, 6, 1, 'w_ice'), box(4.2, 9.5, 10.6, 0.5, 0.8, 'w_mint_glow'),
    box(-3.9, 1, 3, 1, 1, 'w_ice'), box(-4.6, 2, 3, 0.6, 0.8, 'w_ice'), box(-3.9, 5, 7, 1, 1, 'w_ice'), box(-4.6, 6, 7, 0.6, 0.8, 'w_ice'),
    box(0, -10, -9, 2.4, 2.4, 'w_metal'), box(0, -5, -4, 2.4, 2.4, 'w_metal')])
W['rust_glaive'] = (dict(long=True, gui=0.62), [
    box(0, -15.5, -13.5, 1.2, 1.2, 'w_dmetal'), box(0, -13.5, 8, 1.8, 1.8, 'w_rust'), box(0, -7, -3, 2.3, 2.3, 'w_cloth'), box(0, 2, 4, 2.3, 2.3, 'w_cloth'),
    box(0, 7, 9, 3, 3, 'w_brass'), box(2.0, 9, 15, 4.6, 1, 'w_rust_blade'), box(2.6, 15, 16.4, 3.2, 0.9, 'w_rust_blade'), box(3.4, 16.4, 17.2, 1.4, 0.8, 'w_rust_blade'),
    box(-1.2, 9.4, 13.4, 2.2, 1, 'w_rust_blade'), box(-2.9, 9, 11.4, 1.6, 0.9, 'w_rust_blade'), box(-3.6, 8.2, 9.4, 1, 0.8, 'w_rust_blade'),
    box(4.5, 9.6, 15, 0.5, 1.1, 'w_amber_glow'), box(0.8, 10.4, 12, 1.6, 1.6, 'w_amber_glow'), box(0, -11, -10, 2.4, 2.4, 'w_brass')])
W['obelisk_hammer'] = (dict(), [
    box(0, -13, -11.6, 2.8, 2.8, 'w_rune'), box(0, -11.6, 4.5, 1.8, 1.8, 'w_obsidian'), box(0, -9, -6, 2.2, 2.2, 'w_cloth'),
    box(0, 4, 11, 9, 5, 'w_rune'), box(0, 4.5, 10.5, 10.2, 4, 'w_obsidian'), box(0, 11, 13.5, 2.4, 2.4, 'w_obsidian'),
    box(0, 13.5, 14.5, 1.2, 1.2, 'w_amber_glow'), box(0, 3, 4, 3, 3, 'w_obsidian')])
W['control_rod_mace'] = (dict(), [
    box(0, -12, -10.5, 2.8, 2.8, 'w_dmetal'), box(0, -10.5, -3, 2.2, 2.2, 'w_rubber'), box(0, -3, 1, 2, 2, 'w_graphite'),
    box(0, 1, 2, 5.6, 5.6, 'w_hazard'), box(0, 2, 10, 5, 5, 'w_graphite'), box(0, 2.5, 9.5, 6, 4, 'w_graphite'), box(0, 2.5, 9.5, 4, 6, 'w_graphite'),
    box(0, 4, 4.8, 6.4, 4.4, 'w_green_glow'), box(0, 4, 4.8, 4.4, 6.4, 'w_green_glow'), box(0, 7, 7.8, 6.4, 4.4, 'w_green_glow'), box(0, 7, 7.8, 4.4, 6.4, 'w_green_glow'),
    box(0, 10, 11, 3, 3, 'w_dmetal'), box(0, 11, 12, 1.4, 1.4, 'w_green_glow')])
W['flare_saber'] = (dict(), [
    box(0, -12, -11, 2.6, 2.6, 'w_dmetal'), box(0, -11, -4.5, 2, 2, 'w_dmetal'), box(0, -10, -9, 2.4, 2.4, 'w_brass'), box(0, -7, -6, 2.4, 2.4, 'w_brass'),
    box(0, -4.5, -2.4, 3, 3, 'w_brass'), box(0, -2.4, -1.4, 5.6, 1.6, 'w_dmetal'), box(0, -1.4, 12.5, 2.2, 1.2, 'w_flare'),
    box(0, 12.5, 14, 1.4, 1, 'w_flare'), box(0, -3.2, -2.2, 1, 3.2, 'w_amber_glow')])
W['hydra_fang'] = (dict(), [
    box(0, -11.4, -10, 2.6, 2.6, 'w_cyan_crystal'), box(0, -10, -2, 2, 2, 'w_scale'), box(0, -2, -0.6, 5.2, 2.2, 'w_bone'),
    box(2.9, -1.6, 0.6, 0.8, 0.8, 'w_bone'), box(-2.9, -1.6, 0.6, 0.8, 0.8, 'w_bone'),
    box(0.2, -0.6, 4, 3.6, 1.4, 'w_bone'), box(0.6, 4, 8, 3, 1.2, 'w_bone'), box(1.0, 8, 11, 2.2, 1, 'w_bone'), box(1.5, 11, 13.4, 1.2, 0.8, 'w_bone'),
    box(0.2, 0, 9, 0.6, 1.6, 'w_venom_glow')])
W['abyssal_harpoon'] = (dict(long=True, gui=0.62), [
    box(0, -15.5, -14, 1.4, 1.4, 'w_dmetal'), box(0, -14, 8, 1.6, 1.6, 'w_prismarine'), box(0, -10, -5, 2.1, 2.1, 'w_rope'), box(0, -3, -1, 2.8, 2.8, 'w_rope'),
    box(0, 8, 10, 2.4, 2.4, 'w_dmetal'), box(0, 10, 15, 3.4, 1, 'w_steel'), box(0, 15, 16.6, 2, 0.8, 'w_steel'), box(0, 16.6, 17.4, 0.8, 0.6, 'w_steel'),
    box(2.6, 10, 12, 1.8, 0.8, 'w_steel'), box(-2.6, 10, 12, 1.8, 0.8, 'w_steel'), box(3.4, 8.6, 10, 1, 0.8, 'w_steel'), box(-3.4, 8.6, 10, 1, 0.8, 'w_steel'),
    box(0, 11, 14, 0.8, 1.2, 'w_cyan_glow'), box(0, 5.4, 7.2, 2.2, 2.2, 'w_cyan_glow')])
W['pvc_warhammer'] = (dict(), [
    box(0, -13, -11.4, 2.8, 2.8, 'w_pvc'), box(0, -11.4, 5, 1.8, 1.8, 'w_gpipe'), box(0, -3, -1, 2.6, 2.6, 'w_pvc'), box(0, -9, -6, 2.2, 2.2, 'w_hazard'),
    box(0, 5, 10.5, 9.4, 4.6, 'w_pvc'), box(4.9, 4.6, 10.9, 1.4, 5.2, 'w_gpipe'), box(-4.9, 4.6, 10.9, 1.4, 5.2, 'w_gpipe'),
    box(0, 6.8, 8.6, 9.8, 5, 'w_hazard'), box(0, 10.5, 12, 2, 2, 'w_dmetal'), box(0, 12, 12.8, 1.4, 1.4, 'w_lime_glow')])
W['mycelium_scythe'] = (dict(long=True, gui=0.62), [
    box(0, -15.5, -14, 2.2, 2.2, 'w_cap'), box(0, -14, 13, 1.8, 1.8, 'w_hyphae'), box(0, -8, -6.5, 2.4, 2.4, 'w_hyphae'), box(0, 2, 3.5, 2.4, 2.4, 'w_hyphae'),
    box(-4.8, 12.2, 15, 10, 1, 'w_cap'), box(-10.4, 10.8, 14, 2, 1, 'w_cap'), box(-12, 9.6, 12, 1.6, 0.9, 'w_cap'), box(-12.9, 8.8, 10, 0.8, 0.8, 'w_cap'),
    box(-4.8, 11.4, 12.2, 9, 0.7, 'w_gill'), box(-10.4, 10, 10.8, 1.6, 0.7, 'w_gill'),
    box(1.1, 9, 11, 2.2, 2.2, 'w_spore_glow'), box(-1.1, 4, 5.6, 1.6, 1.6, 'w_spore_glow'), box(0, 13, 15.4, 2.8, 2.8, 'w_cap')])

if __name__ == '__main__':
    only = [a for a in sys.argv[1:] if not a.startswith('--')] or list(W)
    for name in only:
        opts, parts = W[name]
        model, tex, glow = build(name, parts, size=64, long=opts.get('long', False), gui=opts.get('gui', 0.9))
        sheet(name, model, tex, glow)
        if '--write' in sys.argv: write(name, model, tex, glow)
    print('ok', only)
