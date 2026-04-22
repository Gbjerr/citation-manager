import { useCallback, useEffect, useState } from 'react';
import {
    SEP,
    type UserCitation,
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
import { API_BASE_URL } from '../../utils/api.ts';
import { FindSourcesCombo } from '../FindSourcesCombo/FindSourcesCombo.tsx';
import { toDateInputValue } from '../../utils/dateUtil.ts';

type CitationEditorProps = {
    tokenPair: TokenPair;
    setTokenPair: (pair: TokenPair) => void;
    clear: () => void;
    selectedCitationList: CitationList | null;
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
    const [citations, setCitations] = useState<UserCitation[]>([]);
    const [bibliography, setBibliography] = useState('');
    const [bibliographyEntries, setBibliographyEntries] = useState<{ text: string, citation: UserCitation }[]>(
        [],
    );
    const [selectedRefStyle, setSelectedRefStyle] =
        useState<ReferenceStyleType>('ieee');

    const [isCreateDialogOpen, setCreateDialogOpen] = useState(false);

    const openCreateDialog = useCallback(() => {
        setCreateDialogOpen(true);
    }, []);

    const closeCreateDialog = useCallback(() => {
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

    const onCreateCitationSubmit = async (citation: UserCitation) => {
        await doCreateCitation(citation);
        closeCreateDialog();
    };

    const doCreateCitation = async (citationToCreate: UserCitation) => {

        try {
            const res = await authorizedFetch(
                tokenPair,
                `${API_BASE_URL}/api/citations`,
                'POST',
                { setTokenPair, clear },
                {
                    title: citationToCreate.title,
                    authors: citationToCreate.authors,
                    publisher: citationToCreate.publisher,
                    date: toDateInputValue(citationToCreate.date),
                    doi: citationToCreate.doi,
                    url: citationToCreate.url,
                    isbn: citationToCreate.isbn,
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

            const created: UserCitation = await res.json();
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
                    `${API_BASE_URL}/api/citations/by-citationlist/${selectedCitationList?.id}`,
                    'GET',
                    { setTokenPair, clear },
                );

                if (!res?.ok) {
                    console.log(res?.status);
                }

                const data = await res?.json();

                const normalizeDateData = (c: UserCitation): UserCitation => {
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

            const bibEntries: { text: string; citation: UserCitation }[] =
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

    const doUpdateCitationData = async (citation: UserCitation) => {
        const res = await authorizedFetch(
            tokenPair,
            `${API_BASE_URL}/api/citations/${citation.id}`,
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

    const doDeleteCitation = async (citation: UserCitation) => {
        const res = await authorizedFetch(
            tokenPair,
            `${API_BASE_URL}/api/citations/${citation.id}`,
            'DELETE',
            { setTokenPair, clear }
        );

        if (!res?.ok) {
            console.log(res?.status)
            return;
        }

        setCitations(citations.filter(c => c.id !== citation.id));
    };

    return (
        <div className="citations-editor">
            <div className="ce-buttonsLayout">
                <button
                    disabled={ selectedCitationList === null }
                    className="createCitationBtn"
                    onClick={openCreateDialog}
                >
                    Add citation
                </button>
                <div className="layoutOptions">
                    <RefStyleCombo
                        onRefStyleSelect={onRefStyleSelect}
                        selectedRefStyle={selectedRefStyle}
                    />
                    <button
                        disabled={ selectedCitationList === null }
                        className="copyBtn"
                        onClick={() =>
                            navigator.clipboard.writeText(bibliography)
                        }
                    >
                        Copy to clipboard
                    </button>
                    <FindSourcesCombo 
                        doCreateCitation={doCreateCitation}
                        selectedCitationList={selectedCitationList}
                    />
                </div>
            </div>

            <CitationDialog
                open={isCreateDialogOpen}
                onClose={closeCreateDialog}
                selectedCitationListTitle={selectedCitationList?.title}
                onSubmit={onCreateCitationSubmit}
            />

            {citations.length != 0 && selectedCitationList ? (
                <ul className={'list-' + selectedCitationList.id}>
                    {bibliographyEntries.map(({ text, citation }) => {
                        return (
                            <li key={citation.id}>
                                <CiteEntry
                                    citation={citation}
                                    text={text}
                                    doUpdateCitationData={doUpdateCitationData}
                                    doDeleteCitation={doDeleteCitation}
                                />
                            </li>
                        );
                    })}
                </ul>
            ) : (
                <h2 className='noCitationsHeader'>No citations</h2>
            )}
        </div>
    );
};
