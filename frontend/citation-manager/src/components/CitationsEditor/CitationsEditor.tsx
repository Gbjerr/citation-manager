import { useCallback, useEffect, useState } from 'react';
import type {
    Citation,
    CitationList,
    ReferenceStyleType,
    TokenPair,
} from '../../types/types';
import authorizedFetch from '../../utils/authorizedFetch';
import { CitationDialog } from '../CitationDialog/CitationDialog';
import { CiteEntry } from '../CiteEntry/CiteEntry';
import { formatCitations } from '../../utils/formatCitations';
import './CitationsEditor.css';
import { RefStyleCombo } from '../RefStyleCombo/RefStyleCombo';

const EMPTY_CITATION: Citation = {
    title: '',
    authors: '',
    publisher: '',
    date: new Date(), // "YYYY-MM-DD"
    doi: '',
    url: '',
    isbn: '',
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
    const [bibliographyEntries, setBibliographyEntries] = useState<string[]>(
        [],
    );
    const [selectedRefStyle, setSelectedRefStyle] =
        useState<ReferenceStyleType>('ieee');

    const [isCreateDialogOpen, setCreateDialogOpen] = useState(false);
    const [newCitation, setNewCitation] = useState<Citation>({
        title: '',
        authors: '',
        publisher: '',
        date: new Date(), // "YYYY-MM-DD"
        doi: '',
        url: '',
        isbn: '',
    });

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
                setCitations(data);
            } catch (e) {
                console.error(
                    e,
                    `Error occurred when fetching citations for list with id=${selectedCitationList?.id}`,
                );
            }
        };

        fetchCitations();
    }, [selectedCitationList]);

    useEffect(() => {
        if (citations.length == 0) return;

        const runFormatting = async () => {
            const listOutputResult = await formatCitations(
                citations,
                selectedRefStyle,
            ).then(result => result);
            setBibliography(listOutputResult);
            setBibliographyEntries(listOutputResult.split('\n'));
        };
        runFormatting();
    }, [citations, selectedRefStyle]);

    return (
        <div className="citations-editor">
            <div className="ce-buttonsLayout">
                <button
                    className="createCitationBtn"
                    onClick={openCreateDialog}
                >
                    Add citation
                </button>
                <RefStyleCombo
                    onRefStyleSelect={onRefStyleSelect}
                    selectedRefStyle={selectedRefStyle}
                />
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
                {citations.map((citation, i) => {
                    return (
                        <li>
                            <CiteEntry
                                citation={citation}
                                text={bibliographyEntries[i]}
                            />
                        </li>
                    );
                })}
            </ul>
        </div>
    );
};
