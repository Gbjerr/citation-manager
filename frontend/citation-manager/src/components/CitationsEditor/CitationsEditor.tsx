import { useCallback, useEffect, useState } from 'react';
import {
    SEP,
    type Citation,
    type CitationList,
    type ReferenceStyleType,
    type TokenPair,
} from '../../types/types';
import authorizedFetch from '../../utils/authorizedFetch';
import { CitationDialog } from '../CitationDialog/CitationDialog';
import { CiteEntry } from '../CiteEntry/CiteEntry';
import { formatCitations } from '../../utils/formatCitations';
import './CitationsEditor.css';
import { RefStyleCombo } from '../RefStyleCombo/RefStyleCombo';

const EMPTY_CITATION: Citation = {
    id: -1,
    title: '',
    authors: '',
    publisher: '',
    date: new Date(), // "YYYY-MM-DD"
    doi: '',
    url: '',
    isbn: '',
    position: -1
};

type CitationEditorProps = {
    tokenPair: TokenPair;
    setTokenPair: (pair: TokenPair) => void;
    clear: () => void;
    selectedCitationList: CitationList;
};

/**
 * Editor for displaying and adding citations to a citation list.
 */
export const CitationsEditor = ({
    tokenPair,
    setTokenPair,
    clear,
    selectedCitationList,
}: CitationEditorProps) => {
    const [citations, setCitations] = useState<Citation[]>([]);
    const [bibliography, setBibliography] = useState('');
    const [bibliographyEntries, setBibliographyEntries] = useState<{ text: string, citation: Citation }[]>(
        [],
    );
    const [selectedRefStyle, setSelectedRefStyle] =
        useState<ReferenceStyleType>('ieee');

    const [isCreateDialogOpen, setCreateDialogOpen] = useState(false);
    const [newCitation, setNewCitation] = useState<Citation>(EMPTY_CITATION);

    const openCreateDialog = useCallback(() => {
        setCreateDialogOpen(true);
    }, []);

    const closeCreateDialog = useCallback(() => {
        setNewCitation(EMPTY_CITATION);
        setCreateDialogOpen(false);
    }, []);

    const onRefStyleSelect = useCallback(
        (refStyle: ReferenceStyleType | null) => {
            if (refStyle !== null) {
                setSelectedRefStyle(refStyle);
            }
        },
        [],
    );

    const doCreateCitation = async () => {
        try {
            const res = await authorizedFetch(
                tokenPair,
                'http://localhost:8090/api/citations',
                'POST',
                { setTokenPair, clear },
                {
                    title: newCitation.title,
                    authors: newCitation.authors,
                    publisher: newCitation.publisher,
                    date: newCitation.date?.toISOString(),
                    doi: newCitation.doi,
                    url: newCitation.url,
                    isbn: newCitation.isbn,
                    position: citations.length === 0
                        ? '1'
                        : `${citations[citations.length - 1].position + 1}`,
                    citationListId: String(selectedCitationList?.id),
                },
            );

            if (!res?.ok) {
                const msg = res
                    ? `${res.status} ${res.statusText}`
                    : 'Request failed';
                throw new Error(msg);
            }

            const created: Citation = await res.json();
            setCitations(prev => [...prev, created]);
        } catch (e) {
            console.error(e, 'Error while creating citation');
        }
    };

    useEffect(() => {
        if (selectedCitationList == null) {
            return;
        }

        const fetchCitations = async () => {
            try {
                const res = await authorizedFetch(
                    tokenPair,
                    `http://localhost:8090/api/citations/by-citationlist/${selectedCitationList?.id}`,
                    'GET',
                    { setTokenPair, clear },
                );

                if (!res?.ok) {
                    console.log(res?.status);
                }

                const data = await res?.json();

                const normalizeDateData = (c: Citation): Citation => {
                    const date = c.date instanceof Date
                        ? c.date
                        : new Date(c.date);

                    return { ...c, date: date };
                }

                setCitations(data.map(normalizeDateData));
            } catch (e) {
                console.error(
                    e,
                    `Error occurred when fetching citations for list with id=${selectedCitationList?.id}`,
                );
            }
        };

        fetchCitations();
    }, [selectedCitationList, tokenPair, setTokenPair, clear]);

    useEffect(() => {
        if (citations.length == 0) {
            setBibliography('');
            setBibliographyEntries([]);
            return;
        }

        const runFormatting = async () => {
            const listOutputResult = await formatCitations(citations, selectedRefStyle);

            const bibEntries: { text: string; citation: Citation }[] =
                listOutputResult.split('\n').map(entry => {
                    const entryParts = entry.split(SEP);
                    const entryId = entryParts[0];
                    const text = entryParts[1].trim();

                    const citation = citations.find(
                        c => c.id === Number(entryId),
                    );
                    if (!citation) {
                        throw new Error(
                            `Failed to find citation with id=${entryId} for bibliography entry: ${entry}`,
                        );
                    }
                    return { text, citation };
                });

            setBibliography(bibEntries.map(entry => entry.text).join('\n'));
            setBibliographyEntries(bibEntries);
        };
        runFormatting();
    }, [citations, selectedRefStyle]);

    const doUpdateCitationData = async (citation: Citation) => {
        const res = await authorizedFetch(
            tokenPair,
            `http://localhost:8090/api/citations/${citation.id}`,
            'PUT',
            { setTokenPair, clear },
            {
                title: citation.title,
                authors: citation.authors,
                publisher: citation.publisher,
                date: citation.date.toISOString(),
                doi: citation.doi,
                url: citation.url,
                isbn: citation.isbn,
                position: `${citation.position}`
            }
        );

        if (!res?.ok) {
            console.log(res?.status);
        }

        // Replace the modified citation
        const modifiedCitations = citations.map(c => {
            if (citation.id == c.id) {
                return citation;
            }

            return c;
        })
        setCitations(modifiedCitations);
    };

    return (
        <div className="citations-editor">
            <div className="ce-buttonsLayout">
                <button
                    className="createCitationBtn"
                    onClick={openCreateDialog}
                >
                    Add citation
                </button>
                <div className='layoutOptions'>
                    <RefStyleCombo
                        onRefStyleSelect={onRefStyleSelect}
                        selectedRefStyle={selectedRefStyle}
                    />
                    <button
                        className="copyBtn"
                        onClick={() => navigator.clipboard.writeText(bibliography)}
                    >
                        Copy to clipboard
                    </button>
                </div>
            </div>

            <CitationDialog
                open={isCreateDialogOpen}
                onClose={closeCreateDialog}
                selectedCitationListTitle={selectedCitationList?.title}
                newCitation={newCitation}
                setNewCitation={setNewCitation}
                onSubmit={() => {
                    void doCreateCitation();
                    closeCreateDialog();
                }}
            />

            <ul className={'list-' + selectedCitationList.id}>
                {bibliographyEntries.map(({ text, citation }) => {
                    return (
                        <li key={citation.id}>
                            <CiteEntry
                                citation={citation}
                                text={text}
                                doUpdateCitationData={doUpdateCitationData}
                            />
                        </li>
                    );
                })}
            </ul>
        </div>
    );
};
