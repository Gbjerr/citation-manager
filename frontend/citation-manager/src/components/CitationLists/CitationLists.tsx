import { useState, useEffect } from 'react';
import type { TokenPair, LoginProps, CitationList } from '../../types/types.ts'
import authorizedFetch from '../../utils/authorizedFetch.ts';

interface CitationListsProps {
    tokenPair: TokenPair,
    setTokenPair: LoginProps['setTokenPair'],
    clear: LoginProps['clear']
}

const CitationLists = ({ tokenPair, setTokenPair, clear }: CitationListsProps) => {
    const [citationLists, setCitationLists] = useState<CitationList[]>([]);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchCitationLists = async () => {
            try {
                const response = await authorizedFetch(tokenPair,
                    'http://localhost:8090/api/citationlists/me',
                    'GET',
                    { setTokenPair, clear }
                );

                if (!response?.ok) {
                    throw new Error(`Failed to fetch citation lists: ${response?.statusText}`)
                }

                const data = await response.json();
                setCitationLists(data);

            } catch (e) {
                setError(e instanceof Error ? e.message : "An error occurred.");
                console.error(e);
            }
        };
        
        fetchCitationLists();
    }, [tokenPair, setTokenPair, clear]);

    const doAddList = async () => {

        const request = await authorizedFetch(tokenPair,
            'http://localhost:8090/api/citationlists/me',
            'POST',
            { setTokenPair, clear },
            {'title': `new list ${citationLists.length + 1}`}
        );

        if(!request?.ok) {
            console.error(request?.statusText);
            setError(request ? request.statusText : "Error")
            return;
        }

        const data = await request.json();
        setCitationLists(prev => [...prev, data]);
    }

    if(error) {
        return <div>Error: {error}</div>
    }

    return <>
        <div>
            <h2>Your citation lists</h2>
            <button onClick={doAddList}>Add new list</button>
            {citationLists.length === 0 ? (
                <p>No citation lists found</p>
            ) : (
                <ul>
                    {citationLists.map((list) => (
                        <li key={list.id}>
                            {list.title}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    </>
}

export default CitationLists;
