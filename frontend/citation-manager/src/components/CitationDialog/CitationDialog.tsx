import { Dialog, DialogPanel, DialogTitle } from '@headlessui/react';
import { memo, useState } from 'react';
import { EMPTY_CITATION, type UserCitation } from '../../types/types';
import './CitationDialog.css';
import { toDateInputValue } from '../../utils/dateUtil';

type CitationDialogProps = {
    open: boolean;
    onClose: () => void;
    selectedCitationListTitle?: string;
    onSubmit: (citation: UserCitation) => void;
};

/**
 * Dialog for creating a new citations with input fields.
 */
export const CitationDialog = memo(function CitationDialog({
    open,
    onClose,
    selectedCitationListTitle,
    onSubmit,
}: CitationDialogProps) {
    const [newCitation, setNewCitation] = useState<UserCitation>(EMPTY_CITATION);

    return (
        <Dialog open={open} onClose={onClose} className="createDialogRoot">
            <div className="createDialogContainer">
                <DialogPanel className="createDialogPanel">
                    <div className="createDialogHeader">
                        <DialogTitle className="createDialogTitle">
                            {selectedCitationListTitle !== undefined
                                ? `Add citation to: ${selectedCitationListTitle}`
                                : 'Edit'}
                        </DialogTitle>
                        <button 
                            className='submitBtn' 
                            type="button" 
                            onClick={() => {
                                onSubmit(newCitation);
                                setNewCitation(EMPTY_CITATION);
                            }}>
                                Submit
                        </button>
                        <button
                            type="button"
                            onClick={() => {
                                setNewCitation(EMPTY_CITATION);
                                onClose();
                            }}
                            className="createDialogClose"
                        >
                            ✕
                        </button>
                    </div>

                    <div className="createDialogBody">
                        <label className="createField">
                            <div>Title</div>
                            <input
                                id="citationTitleInput"
                                className="createInput"
                                value={newCitation.title}
                                onChange={c =>
                                    setNewCitation({
                                        ...newCitation,
                                        title: c.target.value,
                                    })
                                }
                            />
                        </label>
                        <label className="createField">
                            <div>Authors</div>
                            <input
                                id="citationAuthorsInput"
                                className="createInput"
                                value={newCitation.authors}
                                onChange={c =>
                                    setNewCitation({
                                        ...newCitation,
                                        authors: c.target.value,
                                    })
                                }
                            />
                        </label>
                        <label className="createField">
                            <div>Publisher</div>
                            <input
                                id="citationPublisherInput"
                                className="createInput"
                                value={newCitation.publisher}
                                onChange={c =>
                                    setNewCitation({
                                        ...newCitation,
                                        publisher: c.target.value,
                                    })
                                }
                            />
                        </label>
                        <label className="createField">
                            <div>Date</div>
                            <input
                                type="date"
                                id="citationDateInput"
                                className="createInput"
                                value={toDateInputValue(newCitation.date)}
                                onChange={c =>
                                    setNewCitation({
                                        ...newCitation,
                                        date: new Date(c.target.value),
                                    })
                                }
                            />
                        </label>
                        <label className="createField">
                            <div>DOI</div>
                            <input
                                id="citationDoiInput"
                                className="createInput"
                                value={newCitation.doi}
                                onChange={c =>
                                    setNewCitation({
                                        ...newCitation,
                                        doi: c.target.value,
                                    })
                                }
                            />
                        </label>
                        <label className="createField">
                            <div>URL</div>
                            <input
                                id="citationUrlInput"
                                className="createInput"
                                value={newCitation.url}
                                onChange={c =>
                                    setNewCitation({
                                        ...newCitation,
                                        url: c.target.value,
                                    })
                                }
                            />
                        </label>
                        <label className="createField">
                            <div>ISBN</div>
                            <input
                                id="citationIsbnInput"
                                className="createInput"
                                value={newCitation.isbn}
                                onChange={c =>
                                    setNewCitation({
                                        ...newCitation,
                                        isbn: c.target.value,
                                    })
                                }
                            />
                        </label>
                    </div>
                </DialogPanel>
            </div>
        </Dialog>
    );
});
