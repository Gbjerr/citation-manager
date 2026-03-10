import { Dialog, DialogPanel, DialogTitle } from "@headlessui/react";
import { memo } from "react";
import type { Citation } from "../../types/types";

type CitationDialogProps = {
  open: boolean;
  onClose: () => void;
  selectedCitationListTitle?: string;
  newCitation: Citation;
  setNewCitation: (citation: Citation) => void;
  onSubmit: () => void;
};

/**
 * Dialog for creating a new citations with inout fields.
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
    <Dialog open={open} onClose={onClose} className="cm-dialogRoot">
      <div className="cm-dialogContainer">
        <DialogPanel className="cm-dialogPanel">
          <div className="cm-dialogHeader">
            <DialogTitle className="cm-dialogTitle">
              Add citation to: {selectedCitationListTitle}
            </DialogTitle>
            <button
              type="button"
              onClick={onClose}
              className="cm-dialogClose"
            >
              ✕
            </button>
          </div>

          <div className="cm-dialogBody">
            <label className="cm-field">
              <div>Title</div>
              <input
                id="citationTitleInput"
                className="cm-input"
                value={newCitation.title}
                onChange={(c) =>
                  setNewCitation({ ...newCitation, title: c.target.value })
                }
              />
            </label>
            <label className="cm-field">
              <div>Authors</div>
              <input
                id="citationAuthorsInput"
                className="cm-input"
                value={newCitation.authors}
                onChange={(c) =>
                  setNewCitation({ ...newCitation, authors: c.target.value })
                }
              />
            </label>
            <label className="cm-field">
              <div>Publisher</div>
              <input
                id="citationPublisherInput"
                className="cm-input"
                value={newCitation.publisher}
                onChange={(c) =>
                  setNewCitation({ ...newCitation, publisher: c.target.value })
                }
              />
            </label>
            <label className="cm-field">
              <div>Date</div>
              <input
                id="citationDateInput"
                className="cm-input"
                value={newCitation.date}
                onChange={(c) =>
                  setNewCitation({ ...newCitation, date: c.target.value })
                }
              />
            </label>
            <label className="cm-field">
              <div>DOI</div>
              <input
                id="citationDoiInput"
                className="cm-input"
                value={newCitation.doi}
                onChange={(c) =>
                  setNewCitation({ ...newCitation, doi: c.target.value })
                }
              />
            </label>
            <label className="cm-field">
              <div>URL</div>
              <input
                id="citationUrlInput"
                className="cm-input"
                value={newCitation.url}
                onChange={(c) =>
                  setNewCitation({ ...newCitation, url: c.target.value })
                }
              />
            </label>
            <label className="cm-field">
              <div>ISBN</div>
              <input
                id="citationIsbnInput"
                className="cm-input"
                value={newCitation.isbn}
                onChange={(c) =>
                  setNewCitation({ ...newCitation, isbn: c.target.value })
                }
              />
            </label>

            <div className="cm-dialogActions">
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