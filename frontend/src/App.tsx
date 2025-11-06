import { useEffect, useState } from "react";
import { listCriterios, type Criterio } from "./api/criterios";

export default function App() {
    const [items, setItems] = useState<Criterio[]>([]);
    const [loading, setLoading] = useState(true);
    const [err, setErr] = useState<string | null>(null);

    useEffect(() => {
        listCriterios()
            .then(setItems)
            .catch(e => setErr(e?.message ?? "Erro"))
            .finally(() => setLoading(false));
    }, []);

    return (
        <div style={{ padding: 16, fontFamily: "system-ui, sans-serif" }}>
            <h1>MediMetrix — Front</h1>
            <p>API: {import.meta.env.VITE_API_URL}</p>
            {loading && <p>Carregando…</p>}
            {err && <p style={{color:"crimson"}}>Falha ao carregar: {err}</p>}
            {!loading && !err && (
                <>
                    <h2>Critérios (GET /api/v1/criterios)</h2>
                    <ul>{items.map(i => <li key={i.idCriterio}>#{i.idCriterio} — {i.nome}</li>)}</ul>
                </>
            )}
        </div>
    );
}
