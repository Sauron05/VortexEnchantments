import os, re
j = 'src/main/java/com/vortexrpg/enchantments/enchant/impl'
r = 'src/main/resources/enchants'
for cat in sorted(os.listdir(j)):
    cp = os.path.join(j, cat)
    rp = os.path.join(r, cat)
    if not os.path.isdir(cp):
        continue
    ids = set()
    for f in os.listdir(cp):
        if not f.endswith('.java'):
            continue
        txt = open(os.path.join(cp, f), encoding='utf-8').read()
        m = re.search(r'super\("([^"]+)"', txt)
        if m:
            ids.add(m.group(1))
    if not os.path.isdir(rp):
        print(f'NO DIR: {cat}')
        continue
    ymls = {os.path.splitext(f)[0] for f in os.listdir(rp) if f.endswith('.yml')}
    for i in sorted(ids - ymls):
        print(f'MISS: {cat}/{i}.yml')
    for y in sorted(ymls - ids):
        print(f'ORPHAN: {cat}/{y}.yml')
print('DONE')
