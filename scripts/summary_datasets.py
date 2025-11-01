import json, os
from glob import glob

def is_cyclic(n, edges):
    adj = [[] for _ in range(n)]
    for u,v,_ in edges:
        adj[u].append(v)
    visited = [0]*n
    def dfs(u):
        visited[u]=1
        for v in adj[u]:
            if visited[v]==1:
                return True
            if visited[v]==0 and dfs(v):
                return True
        visited[u]=2
        return False
    for i in range(n):
        if visited[i]==0 and dfs(i):
            return True
    return False

files = sorted(glob('data/*.json'))
rows = []
for f in files:
    with open(f,'r',encoding='utf-8') as fh:
        txt = fh.read()
        # strip optional comments
        try:
            data = json.loads(txt)
        except Exception as e:
            # try to remove // comments lines
            lines = [l for l in txt.splitlines() if not l.strip().startswith('//')]
            data = json.loads('\n'.join(lines))
    n = data.get('n')
    edges = data.get('edges', [])
    m = len(edges)
    cyc = is_cyclic(n, [(e['u'], e['v'], e.get('w',1)) for e in edges])
    rows.append((os.path.basename(f), n, m, 'cyclic' if cyc else 'acyclic'))

print('name,n,m,type')
for r in rows:
    print(','.join(map(str,r)))

