import { Dialog, DialogPanel, DialogTitle } from '@headlessui/react';
import { memo } from 'react';
import type { Citation } from '../../types/types';
import './CitationDialog.css';

type CitationDialogProps = {
    open: boolean;
    onClose: () => void;
    selectedCitationListTitle?: string;
    newCitation: Citation;
    setNewCitation: (citation: Citation) => void;
    onSubmit: () => void;
};

/**
 * Dialog for creating a new citations with input fields.
 */
export const CitationDialog = memo(function CitationDialog({
    open,
    onClose,
    selectedCitationListTitle,
    newCitation,
    setNewCitation,
    onSubmit,
}: CitationDialogProps) {
    return (
        <Dialog open={open} onClose={onClose} className="createDialogRoot">
            <div className="createDialogContainer">
                <DialogPanel className="createDialogPanel">
                    <div className="createDialogHeader">
                        <DialogTitle className="createDialogTitle">
                            {selectedCitationListTitle !== undefined
                                ? `Add citation to: ${selectedCitationListTitle}`
                                : ''}
                        </DialogTitle>
                        <button
                            type="button"
                            onClick={onClose}
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
                                value={
                                    newCitation.date.toISOString().split('T')[0]
                                }
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

                        <div className="createDialogActions">
                            <button type="button" onClick={onClose}>
                                Close
                            </button>
                            <button type="button" onClick={onSubmit}>
                                Submit
                            </button>
                        </div>
                    </div>
                </DialogPanel>
            </div>
        </Dialog>
    );
});
