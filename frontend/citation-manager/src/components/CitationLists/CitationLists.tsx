import { useState, useEffect } from 'react';
import type { TokenPair, LoginProps, CitationList } from '../../types/types.ts';
import authorizedFetch from '../../utils/authorizedFetch.ts';
import './CitationLists.css';

interface CitationListsProps {
    tokenPair: TokenPair;
    setTokenPair: LoginProps['setTokenPair'];
    clear: LoginProps['clear'];
    setSelectedCitationList: (list: CitationList) => void;
}

/**
 * Section for displaying the citation lists of a user.
 */
const CitationLists = ({
    tokenPair,
    setTokenPair,
    clear,
    setSelectedCitationList,
}: CitationListsProps) => {
    const [citationLists, setCitationLists] = useState<CitationList[]>([]);
    const [newCitationListName, setNewCitationListName] = useState<string>('');
    const [error, setError] = useState<string | null>(null);
    const [selectedListId, setSelectedListId] = useState<number | null>(null);

    useEffect(() => {
        const fetchCitationLists = async () => {
            try {
                const response = await authorizedFetch(
                    tokenPair,
                    'http://localhost:8090/api/citationlists/me',
                    'GET',
                    { setTokenPair, clear },
                );

                if (!response?.ok) {
                    throw new Error(
                        `Failed to fetch citation lists: ${response?.statusText}`,
                    );
                }

                const data = await response.json();
                setCitationLists(data);
            } catch (e) {
                setError(
                    e instanceof Error
                        ? e.message
                        : 'An error occurred, please reload the page.',
                );
                console.error(e);
            }
        };

        fetchCitationLists();
    }, [tokenPair, setTokenPair, clear]);

    const doAddList = async () => {
        if (newCitationListName.trim() === '') {
            return;
        }

        const request = await authorizedFetch(
            tokenPair,
            'http://localhost:8090/api/citationlists/me',
            'POST',
            { setTokenPair, clear },
            { title: newCitationListName },
        );

        if (!request?.ok) {
            console.error(request?.statusText);
            setError(request ? request.statusText : 'Error');
            return;
        }

        const data = await request.json();
        setCitationLists(prev => [...prev, data]);
        setNewCitationListName('');
    };

    if (error) {
        return <div>Error: {error}</div>;
    }

    return (
        <>
            <div>
                <h2 className="citationListsHeader">Your citation lists</h2>
                <input
                    id="citationListNameInput"
                    className="citationListNameInput"
                    type="text"
                    placeholder="Enter new citation list here"
                    onChange={e => setNewCitationListName(e.target.value)}
                />
                <button className="doAddBtn" onClick={doAddList}>+</button>
                {citationLists.length === 0 ? (
                    <p>No citation lists found</p>
                ) : (
                    <ul className="listOfLists">
                        {citationLists.map(list => (
                            <li key={list.id}>
                                <button
                                    className={
                                        'citationListBtn' +
                                        (selectedListId === list.id
                                            ? ' citationListBtn--selected'
                                            : '')
                                    }
                                    onClick={() => {
                                        setSelectedListId(list.id);
                                        setSelectedCitationList({
                                            id: list.id,
                                            title: list.title,
                                        });
                                    }}
                                >
                                    {list.title}
                                </button>
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </>
    );
};

export default CitationLists;
