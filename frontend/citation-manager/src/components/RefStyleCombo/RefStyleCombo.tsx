import {
    Combobox,
    ComboboxButton,
    ComboboxInput,
    ComboboxOption,
    ComboboxOptions,
} from '@headlessui/react';
import { useMemo, useState } from 'react';
import { ChevronDownIcon } from '@heroicons/react/20/solid';
import {
    REFERENCE_STYLE_VALUES,
    type ReferenceStyleType,
} from '../../types/types';
import './RefStyleCombo.css';

type RefStyleComboProps = {
    selectedRefStyle: ReferenceStyleType;
    onRefStyleSelect: (refStyle: ReferenceStyleType | null) => void;
};

/**
 * Combo box component for selecting reference style of the citation list.
 */
export const RefStyleCombo = ({
    selectedRefStyle,
    onRefStyleSelect,
}: RefStyleComboProps) => {
    const [query, setQuery] = useState('');

    // Retrieves filtered reference styles based on current query.
    const filteredStyles = useMemo(() => {
        const q = query.trim().toLowerCase();
        if (!q) return REFERENCE_STYLE_VALUES;
        return REFERENCE_STYLE_VALUES.filter(style => style.includes(q));
    }, [query]);

    return (
        <Combobox value={selectedRefStyle} onChange={onRefStyleSelect}>
            <div className="refStyleCombo">
                <div className="relative">
                    <div className="comboInput">
                        <div className="comboTitle">Reference style</div>
                        <ComboboxInput
                            className="refStyleComboInput"
                            displayValue={(style: ReferenceStyleType) => style}
                            onChange={e => setQuery(e.target.value)}
                            placeholder="Reference style…"
                            autoComplete="off"
                        />
                    </div>

                    <ComboboxButton className="refStyleComboButton">
                        <ChevronDownIcon className="downIcon" />
                    </ComboboxButton>
                </div>

                <ComboboxOptions
                    anchor="bottom"
                    className="refStyleComboOptions"
                >
                    {filteredStyles.length === 0 ? (
                        <div className="refStyleComboEmpty">No matches</div>
                    ) : (
                        filteredStyles.map(style => (
                            <ComboboxOption
                                key={style}
                                value={style}
                                className="refStyleComboOption"
                            >
                                {({ selected }) => (
                                    <span
                                        className={
                                            'refStyleComboOptionText' +
                                            (selected
                                                ? ' refStyleComboOptionText--selected'
                                                : '')
                                        }
                                    >
                                        {style}
                                    </span>
                                )}
                            </ComboboxOption>
                        ))
                    )}
                </ComboboxOptions>
            </div>
        </Combobox>
    );
};