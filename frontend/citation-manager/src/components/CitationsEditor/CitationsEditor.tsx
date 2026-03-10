import { memo, useCallback, useEffect, useState } from "react";
import type { Citation, CitationList, TokenPair } from "../../types/types";
import authorizedFetch from "../../utils/authorizedFetch";
import { CitationDialog } from "../CitationDialog/CitationDialog";

const EMPTY_CITATION: Citation = {
  title: '',
  authors: '',
  publisher: '',
  date: '', // "YYYY-MM-DD"
  doi: '',
  url: '',
  isbn: ''
};

type CitationEditorProps = {
    tokenPair: TokenPair,
    setTokenPair: (pair: TokenPair) => void,
    clear: () => void,
    selectedCitationList: CitationList
}

/**
 * Editor for displaying and adding citations to a citation list.
 */
export const CitationsEditor = ({ tokenPair, setTokenPair, clear, selectedCitationList }: CitationEditorProps) => {
    const [citations, setCitations] = useState<Citation[]>([]);

    const [isDialogOpen, setDialogOpen] = useState(false);
    const [newCitation, setNewCitation] = useState<Citation>({
        title: '',
        authors: '',
        publisher: '',
        date: '', // "YYYY-MM-DD"
        doi: '',
        url: '',
        isbn: ''
    });

    const openDialog = useCallback(() => {
        setDialogOpen(true);
    }, []);

    const closeDialog = useCallback(() => {
        setNewCitation(EMPTY_CITATION);
        setDialogOpen(false);
    }, []);

    const doCreateCitation = async() => {
        try {

            // Adjust this URL/body if your backend expects a different endpoint or field names
            const res = await authorizedFetch(
                tokenPair,
                'http://localhost:8090/api/citations',
                'POST',
                { setTokenPair, clear },
                {
                    title: newCitation.title,
                    authors: newCitation.authors,
                    publisher: newCitation.publisher,
                    date: newCitation.date, // send "" or "YYYY-MM-DD" depending on backend; see note below
                    doi: newCitation.doi,
                    url: newCitation.url,
                    isbn: newCitation.isbn,
                    citationListId: String(selectedCitationList?.id),
                }
            );

            if (!res?.ok) {
                const msg = res ? `${res.status} ${res.statusText}` : 'Request failed';
                throw new Error(msg);
            }

            const created: Citation = await res.json();
            setCitations(prev => [...prev, created]);

        } catch (e) {
            console.error(e, 'Error while creating citation')
        }
    }

    useEffect(() => {
        if(selectedCitationList == null) {
            return;
        }

        const fetchCitations = async () => {

            try {
                const res = await authorizedFetch(
                    tokenPair, 
                    `http://localhost:8090/api/citations/by-citationlist/${selectedCitationList?.id}`,
                    'GET',
                    {setTokenPair, clear}
                );

                if(!res?.ok) {
                    console.log(res?.status)
                }

                const data = await res?.json();
                setCitations(data);
            
            } catch(e) {
                console.error(e, `Error occurred when fetching citations for list with id=${selectedCitationList?.id}`)
            }
        };

        fetchCitations();

    }, [selectedCitationList])


  return (
    <div className="citations-editor">
      <button onClick={openDialog}>Add citation</button>

      <CitationDialog
        open={isDialogOpen}
        onClose={closeDialog}
        selectedCitationListTitle={selectedCitationList?.title}
        newCitation={newCitation}
        setNewCitation={setNewCitation}
        onSubmit={() => {
          void doCreateCitation();
          closeDialog();
        }}
      />

      <ul className={"list-" + selectedCitationList.id}>
        {citations.map((citation) => {
          return (
            <ul>
              <li>{citation?.title}</li>
              <li>{citation?.authors}</li>
              <li>{citation?.publisher}</li>
              <li>{citation?.date}</li>
              <li>{citation?.doi}</li>
              <li>{citation?.url}</li>
            </ul>
          );
        })}
      </ul>
    </div>
  );
};
