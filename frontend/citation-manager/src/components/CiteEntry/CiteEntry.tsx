import type { Citation } from '../../types/types';
import './CiteEntry.css';

type CiteEntryProps = {
    citation: Citation;
    text: string;
};
/**
 * Represents a citation entry in a list.
 */
export const CiteEntry = ({ citation, text }: CiteEntryProps) => {
    return (
        <span>
            <div className="citationText">{text}</div>
            <button className="editButton" onClick={() => {}}>
                edit...
            </button>
        </span>
    );
};
