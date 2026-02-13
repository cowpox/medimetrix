import { http } from "./http";
export type Criterio = { idCriterio: number; nome: string };

export async function listCriterios() {
    const { data } = await http.get<Criterio[]>("/api/v1/criterios");
    return data;
}
