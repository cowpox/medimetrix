import { Link, Outlet } from "react-router-dom";

export default function Layout() {
    return (
        <div style={{display:"grid", gridTemplateColumns:"220px 1fr", minHeight:"100vh", fontFamily:"system-ui,sans-serif"}}>
            <aside style={{padding:16, borderRight:"1px solid #eee"}}>
                <h2 style={{marginTop:0}}>MediMetrix</h2>
                <nav style={{display:"grid", gap:8}}>
                    <Link to="/admin">Admin</Link>
                    <Link to="/gestor">Gestor</Link>
                    <Link to="/medico">Médico</Link>
                    <Link to="/">Sair / Login</Link>
                </nav>
            </aside>
            <main style={{padding:24}}>
                <Outlet/>
            </main>
        </div>
    );
}
