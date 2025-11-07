import React from "react";

const App: React.FC = () => {
  return (
    <div className="app">
      <header className="app__header">
        <h1>Medimetrix Frontend</h1>
        <p>React application scaffold ready for development.</p>
      </header>
      <main className="app__content">
        <section>
          <h2>Getting Started</h2>
          <ol>
            <li>Run <code>npm install</code> to install dependencies.</li>
            <li>Start the development server with <code>npm run dev</code>.</li>
            <li>Edit <code>src/App.tsx</code> to begin building your UI.</li>
          </ol>
        </section>
      </main>
    </div>
  );
};

export default App;
