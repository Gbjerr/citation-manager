import { useCallback, useState } from 'react';
import type { Citation } from '../../types/types';
import './CiteEntry.css';
import { CitationDialog } from '../CitationDialog/CitationDialog';

type CiteEntryProps = {
    citation: Citation;
    text: string;
    doUpdateCitationData: (citation: Citation) => void;
};

/** Checks for equality in attributes of two citations. */
function citationEquals(a: Citation, b: Citation): boolean {
    return (
        a.title === b.title &&
        a.authors === b.authors &&
        a.publisher === b.publisher &&
        a.doi === b.doi &&
        a.url === b.url &&
        a.isbn === b.isbn &&
        a.date === b.date
    );
}

/**
 * Represents a citation entry in a list.
 */
export const CiteEntry = ({ citation, text, doUpdateCitationData }: CiteEntryProps) => {
    const [currentCitation, setCurrentCitation] = useState(citation);
    const [isUpdateDialogOpen, setUpdateDialogOpen] = useState(false);

    const openUpdateDialog = useCallback(() => {
        setUpdateDialogOpen(true);
    }, []);
    const closeUpdateDialog = useCallback(() => {
        setUpdateDialogOpen(false);
    }, []);

    return (
        <div className='citeEntry'>
            <CitationDialog
                open={isUpdateDialogOpen}
                onClose={closeUpdateDialog}
                newCitation={currentCitation}
                setNewCitation={setCurrentCitation}
                onSubmit={() => {
                    if (!citationEquals(citation, currentCitation)) {
                        doUpdateCitationData(currentCitation);
                    }
                    closeUpdateDialog();
                }}
            />

            <span>
                <div className="citationText">{text}</div>
                <button className="editButton" onClick={() => {
                    openUpdateDialog();
                }}>
                    edit...
                </button>
            </span>
        </div>
    );
};
