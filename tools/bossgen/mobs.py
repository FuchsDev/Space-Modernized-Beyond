"""Spezifikation der Dungeon-Mobs (je Mob ein Modul mob_<name>.py, das MOBS[name] = dict(...) eintraegt).
Format wie bei den Bossen (specs.py): java, tex, scale, roots, doc, poses {1: Schlag, 2: Schuss, 3: Spezial}."""
import importlib, os

MOBS = {}
ORDER = ['moon_drone', 'lost_astronaut', 'frost_wisp', 'glacier_brute', 'rust_scuttler', 'scrap_gunner', 'void_shade', 'tomb_warden',
         'irradiated_ghoul', 'toxic_sludge', 'cinder_imp', 'magma_hound', 'crystal_mantis', 'schrab_spitter', 'deep_angler', 'abyss_diver',
         'polymer_ooze', 'refinery_drone', 'hive_wasp']

def load(only=None):
    here = os.path.dirname(os.path.abspath(__file__))
    for n in ORDER:
        if only and n not in only: continue
        if os.path.exists(os.path.join(here, 'mob_%s.py' % n)):
            importlib.import_module('mob_%s' % n)
    return MOBS
