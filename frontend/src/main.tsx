import React from "react";
import ReactDOM from "react-dom/client";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import Layout from "./components/Layout";
import Admin from "./pages/Admin";
import Gestor from "./pages/Gestor";
import Medico from "./pages/Medico";
import Login from "./pages/Login";
import NotFound from "./pages/NotFound";

const qc = new QueryClient();
const router = createBrowserRouter([
    { path: "/", element: <Layout />, children: [
            { index: true, element: <Login /> },
            { path: "admin/*", element: <Admin /> },
            { path: "gestor/*", element: <Gestor /> },
            { path: "medico/*", element: <Medico /> },
            { path: "*", element: <NotFound /> },
        ]},
]);

ReactDOM.createRoot(document.getElementById("root")!).render(
    <React.StrictMode>
        <QueryClientProvider client={qc}>
            <RouterProvider router={router}/>
        </QueryClientProvider>
    </React.StrictMode>
);
