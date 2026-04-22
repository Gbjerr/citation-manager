import type { SemanticCitation, UserCitation } from "../../types/types"
import { toDateInputValue } from "../../utils/dateUtil"
import './CitationResult.css';


/**
 * Component for displaying a single search result from a semantic search.
 */
type CitationResultProps = {
    citation: SemanticCitation;
    onAdd: (citation: UserCitation) => void;
}

export const CitationResult = ({ citation, onAdd }: CitationResultProps) => {

    return (
        <div className="citationResultContatainer">

            <button className="addButton" onClick={() => {
                const userCitation: UserCitation = {
                    title: citation.title,
                    authors: citation.authors,
                    publisher: citation.publisher,
                    date: citation.date,
                    doi: citation.doi,
                    url: citation.url,
                    isbn: '',
                    position: -1,
                    citationListId: -1,
                    id: -1
                };
                onAdd(userCitation);
            }}>
                Add to current list
            </button>

            <h3>{citation.title}</h3>
            <p>{citation.authors}</p>
            <p>{citation.publisher}</p>
            <p>{toDateInputValue(citation.date)}</p>
            <p>{citation.doi}</p>
            <p>{citation.url}</p>
            <p>{citation.abstractText}</p>
        </div>
    )
}