import { http } from "./http";
export type Avaliacao = { idAvaliacao:number; titulo:string; status:string };
export async function listAvaliacoes(params?: { page?:number; size?:number }) {
    const { data } = await http.get<Avaliacao[]>("/api/v1/avaliacoes", { params });
    return data;
}
