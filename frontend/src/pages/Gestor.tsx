import { useQuery } from "@tanstack/react-query";
import { listAvaliacoes } from "../api/avaliacao";

export default function Gestor(){
    const { data, isLoading, error } = useQuery({ queryKey:["avaliacoes"], queryFn: () => listAvaliacoes() });
    if (isLoading) return <p>Carregando…</p>;
    if (error) return <p style={{color:"crimson"}}>Falha ao carregar</p>;
    return (
        <>
            <h1>Gestor — Avaliações</h1>
            <ul>{data?.map(a => <li key={a.idAvaliacao}>#{a.idAvaliacao} — {a.titulo} ({a.status})</li>)}</ul>
        </>
    );
}
