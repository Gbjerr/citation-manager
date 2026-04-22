import {
    Combobox,
    ComboboxButton,
    ComboboxOption,
    ComboboxOptions,
} from '@headlessui/react';
import './FindSourcesCombo.css';
import { ChevronDownIcon } from '@heroicons/react/20/solid';
import { useCallback, useState } from 'react';
import { type CitationList, type UserCitation } from '../../types/types';
import { SearchCitationDialog } from '../SearchCitationDialog/SearchCitationDialog';

/**
 * Combo box for aiding in finding sources for a citaiton list.
 */
type FindSourcesComboProps = {
    doCreateCitation: (citation: UserCitation) => Promise<void>;
    selectedCitationList: CitationList | null;
};

export const FindSourcesCombo = ({
    doCreateCitation,
    selectedCitationList,
}: FindSourcesComboProps) => {
    const SEARCH_DESCRIPTION = 'search-description';
    // TODO: implement recommend sources functionality
    const RECOMMEND = 'recommend';

    const [isSearchDialogOpen, setIsSearchDialogOpen] = useState(false);

    const onCloseSearchDialog = useCallback(() => {
        setIsSearchDialogOpen(false);
    }, []);

    const onActionSelected = useCallback((action: string | null) => {
        if (action === SEARCH_DESCRIPTION) {
            setIsSearchDialogOpen(true);
        }
        // TODO: implement recommend sources functionality
    }, []);

    return (
        <div className="findSourcesComboContainer">
            <SearchCitationDialog
                isOpen={isSearchDialogOpen}
                onClose={onCloseSearchDialog}
                onSubmit={citation => {
                    void doCreateCitation(citation);
                    onCloseSearchDialog();
                }}
            />

            <Combobox onChange={onActionSelected}>
                <div className="findSourcesCombo">
                    <ComboboxOptions className="findSourcesOptions">
                        <ComboboxOption value={SEARCH_DESCRIPTION}>
                            <div className="searchByDescriptionContent">
                                Search by description
                            </div>
                        </ComboboxOption>
                    </ComboboxOptions>
                    <ComboboxButton 
                        className={`findSourcesComboButton${selectedCitationList === null ? '--disabled' : ''}`} 
                        disabled={selectedCitationList === null}>
                            <div className="findSourcesTextBtnContent">
                                <div className="findSourcesButtonText">
                                    Find sources
                                </div>
                                <ChevronDownIcon className="downIcon" />
                            </div>
                    </ComboboxButton>
                </div>
            </Combobox>
        </div>
    );
};
