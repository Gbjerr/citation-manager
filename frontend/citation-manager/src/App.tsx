import { useState } from 'react';
import useTokenPair from './hooks/useTokenPair'
import Login from './components/Login/Login';
import CitationLists from './components/CitationLists/CitationLists';
import type { CitationList } from './types/types.ts'
import './App.css'
import { CitationsEditor } from './components/CitationsEditor/CitationsEditor.tsx';
import { Navbar } from './components/Navbar/Navbar.tsx';


const App = () => {
    const { tokenPair, setTokenPair, isAuthenticated, clear } = useTokenPair();
    const [selectedCitationList, setSelectedCitationList] =
        useState<CitationList | null>(null);

    // De-select the citation list on clear.
    const handleClear = () => {
        setSelectedCitationList(null);
        clear();
    };

    return (
        <div className="app">
            <Navbar
                isAuthenticated={isAuthenticated}
                tokenPair={tokenPair}
                clear={handleClear}
            />

            {isAuthenticated ? (
                <div className="app-wrapper">
                    <aside className="citation-lists-wrapper">
                        <CitationLists
                            tokenPair={tokenPair}
                            setTokenPair={setTokenPair}
                            clear={handleClear}
                            setSelectedCitationList={setSelectedCitationList}
                        />
                    </aside>
                    <main className="main-content">
                        {selectedCitationList == null ? (
                            <h2>Select a citation list.</h2>
                        ) : (
                            <CitationsEditor
                                tokenPair={tokenPair}
                                setTokenPair={setTokenPair}
                                clear={handleClear}
                                selectedCitationList={selectedCitationList}
                            />
                        )}
                    </main>
                </div>
            ) : (
                <Login setTokenPair={setTokenPair} clear={clear} />
            )}
        </div>
    );
};

export default App
