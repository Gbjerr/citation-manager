import { Dialog, DialogPanel, DialogTitle } from '@headlessui/react';
import type { SemanticCitation, UserCitation } from '../../types/types';
import './SearchCitationDialog.css';
import { useState } from 'react';
import { API_BASE_URL } from '../../utils/api';
import { CitationResult } from '../CitationResult/CitationResult';
import { DotSpinner } from 'ldrs/react'
import 'ldrs/react/DotSpinner.css';

/**
 * Dialog for doing semantic search for citation by description.
 */
const MIN_HELIX_MS = 600;
const sleep = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

type SearchCitationDialogProps = {
    isOpen: boolean;
    onClose: () => void;
    onSubmit: (citation: UserCitation) => void;
};
export const SearchCitationDialog = ({
    isOpen,
    onClose,
    onSubmit,
}: SearchCitationDialogProps) => {
    const [searchText, setSearchText] = useState<string>('');
    const [similarCitations, setSimilarCitations] = useState<
        SemanticCitation[]
    >([]);
    const [isLoading, setIsLoading] = useState<boolean>(false);

    const onSearch = async () => {
        if (isLoading) return;

        const startedAt = Date.now();
        setIsLoading(true);

        try {
            const res = await fetch(
                `${API_BASE_URL}/api/semanticcitations/similarity`,
                {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ input: searchText }),
                },
            );

            if (!res.ok) {
                console.error(res);
                return;
            }

            const data: SemanticCitation[] = await res.json();
            setSimilarCitations(data);
        } catch(e) {
            console.error(e);
        } finally {
            const elapsed = Date.now() - startedAt;
            const remaining = Math.max(0, MIN_HELIX_MS - elapsed);
            if (remaining > 0) await sleep(remaining);
            setIsLoading(false);
        }
    };

    return (
        <Dialog
            open={isOpen}
            onClose={onClose}
            className="searchCitationDialogRoot"
        >
            <div className="searchCitationDialogContainer">
                <DialogPanel className="searchCitationDialogPanel">
                    <div className="searchCitationDialogHeader">
                        <DialogTitle className="searchCitationDialogTitle">
                            Search for sources
                        </DialogTitle>
                        <button
                            type="button"
                            onClick={() => {
                                setSimilarCitations([]);
                                setSearchText('');
                                onClose();
                            }}
                            className="searchCitationDialogClose"
                        >
                            ✕
                        </button>
                    </div>

                    <div className="searchCitationDialogBody">
                        <textarea
                            className="descriptionSearchInput"
                            placeholder="Search by description"
                            onChange={e => setSearchText(e.target.value)}
                        />

                        <div className='loadButtonWrapper'>
                            {isLoading &&
                                <div className='dotSpinnerWrapper'>
                                    <DotSpinner
                                        size="34"
                                        speed="0.9" 
                                        color="black" 
                                    />
                                </div>
                            }
                            <button
                                className="descriptionSearchBtn"
                                type="button"
                                onClick={onSearch}
                            >
                                Search
                            </button>
                        </div>

                        <div className="similarCitationsContainer">
                            {similarCitations.map(citation => (
                                <CitationResult
                                    citation={citation}
                                    onAdd={(citation: UserCitation) => {
                                        onSubmit(citation);
                                        setSimilarCitations([]);
                                        setSearchText('');
                                    }}
                                />
                            ))}
                        </div>
                    </div>
                </DialogPanel>
            </div>
        </Dialog>
    );
};
