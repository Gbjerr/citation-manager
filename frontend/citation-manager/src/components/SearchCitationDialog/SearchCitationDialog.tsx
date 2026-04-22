import { Dialog, DialogPanel, DialogTitle } from '@headlessui/react';
import type { SemanticCitation, UserCitation } from '../../types/types';
import './SearchCitationDialog.css';
import { useState } from 'react';
import { API_BASE_URL } from '../../utils/api';
import { CitationResult } from '../CitationResult/CitationResult';

/**
 * Dialog for doing semantic search for citation by description.
 */
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

    const onSearch = async () => {
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

        res.json().then((data: SemanticCitation[]) => {
            setSimilarCitations(data);
        });
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
                        <button
                            className="descriptionSearchBtn"
                            type="button"
                            onClick={onSearch}
                        >
                            Search
                        </button>

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
