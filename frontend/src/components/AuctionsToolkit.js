import {Dropdown, FormControl, InputGroup,Button} from "react-bootstrap";
import React, {useEffect, useState} from "react";
import ReactMultiSelectCheckboxes from 'react-multiselect-checkboxes';
import AuctionService from "../services/AuctionService";
import {faSearch} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

const AuctionsToolkit = (props) => {
    const [displayableSort, setDisplayableSort] = useState('Most popular (DESC)');
    const [searchBarValue, setSearchBarValue] = useState('');

    const statuses = {
        DRAFT: 'DRAFT',
        WAITING: 'WAITING',
        RUNNING: 'RUNNING',
        FINISHED: 'FINISHED'
    }

    const [sortValues, setSortValues] = useState({
        type: 'DESC',
        field: 'userLikesCount'
    })

    const [filterValues, setFilterValues] = useState({
            types: ['creator', 'subscriber', 'other'],
            statuses: [],
            categories: [],
            tags: [],
            words: []
        }
    );

    useEffect(() => {
        handleSearch();
    }, []);

    const sortOptions = [{
        id: 0,
        sortDataField: 'userLikesCount',
        sortName: 'Most popular'
    }, {
        id: 1,
        sortDataField: 'subscribedUsersCount',
        sortName: 'Members'
    }, {
        id: 2,
        sortDataField: 'beginDateTime',
        sortName: 'Begin date'
    }, {
        id: 3,
        sortDataField: 'avgMinPrice',
        sortName: 'Average opening price'
    }]

    const filterOptions = [
        // type
        { label: 'My auctions', value: 'creator', field: 'type'},
        { label: 'My subscriptions', value: 'subscriber', field: 'type'},
        { label: 'Other auctions', value: 'other', field: 'type'},
        // status
        { label: 'DRAFT', value: statuses.DRAFT, field: 'status'},
        { label: 'WAITING', value: statuses.WAITING, field: 'status'},
        { label: 'RUNNING', value: statuses.RUNNING, field: 'status'},
        { label: 'FINISHED', value: statuses.FINISHED, field: 'status'},
        // categories
        { label: 'ART', value: 'ART', field: 'category'},
        { label: 'ENTERTAINMENT', value: 'ENTERTAINMENT', field: 'category'},
        { label: 'SCIENCE', value: 'SCIENCE', field: 'category'},
        { label: 'LIFE', value: 'LIFE', field: 'category'},

    ];

    const setCurrentDisplayableSort = (sortDataField, sortType) =>{
        setDisplayableSort(sortOptions.find(el=>el.sortDataField === sortDataField).sortName + ' (' + sortType + ')');
    }

    const handleChangeSortField = (newSortDataField) => {
        setSortValues({...sortValues, field: newSortDataField})
        setCurrentDisplayableSort(newSortDataField, sortValues.type);
    }

    const handleChangeSortType = (e) => {
        setSortValues({...sortValues, type: e.target.value})
        setCurrentDisplayableSort(sortValues.field, e.target.value);
    }

    const setCheckBoxFilterValues = (values) => {
        let types = [];
        let statuses = [];
        let categories = [];
        values.forEach(el=>{
            switch (el.field) {
                case 'type':
                    types.push(el.value);
                    break;
                case 'status':
                    statuses.push(el.value);
                    break;
                case 'category':
                    categories.push(el.value);
                    break;
            }
        });
        const newValues = {...filterValues, types: types, statuses: statuses, categories: categories};
        setFilterValues(newValues);
        return newValues;
    }

    const setSearchFilterValues = () => {
        let tags = [];
        let words = [];
        searchBarValue.trim().split(' ').forEach(el=>{
            if(el[0] === '#'){
                tags.push(el.split('#').join(''));
            }
            else if(el !== ''){
                words.push(el);
            }
        });
        const newValues = {...filterValues, tags: tags, words: words};
        setFilterValues(newValues);
        return newValues;
    }

    const buildRequestData = (filterValues) => {
        return {
            filterList: [
                {operation: 'LIKE', property: 'nameAndDescription', orPredicate: 'true', values: filterValues.words},
                {operation: 'EQUAL', property: 'special', orPredicate: 'false', values: filterValues.types},
                {operation: 'EQUAL', property: 'categories', orPredicate: 'true', values: filterValues.categories},
                {operation: 'EQUAL', property: 'tags', orPredicate: 'true', values: filterValues.tags},
                {operation: 'EQUAL', property: 'status', orPredicate: 'true', values: filterValues.statuses}
            ].filter(filter=>filter.values.length !== 0),
            orPredicate: 'false',
            sortList: [{
                direction: sortValues.type,
                property: sortValues.field
            }
            ]
        };
    }

    const handleSearch = () => {
        console.log('Request',buildRequestData(setSearchFilterValues()));
        AuctionService.searchAuctions(buildRequestData(setSearchFilterValues()))
            .then((response)=>{console.log(response.data.content);props.setData(response.data.content)})
    }

    return (
        <div className='row' style={{marginBottom: '10px'}}>
            <div className='col-sm-4'>
                <div style={{display: 'inline-block', width: '70%'}}>
                <InputGroup>
                    <InputGroup.Text>
                        <FontAwesomeIcon icon={faSearch} size="sm"/>
                    </InputGroup.Text>
                    <FormControl placeholder={'Any word or tag starts with \'#\''}
                        onChange={(e)=>setSearchBarValue(e.target.value)}/>
                </InputGroup>
            </div>
                <Button variant={"warning"}
                        style={{marginLeft: '10px', marginBottom: '6px'}}
                        onClick={handleSearch}>
                    Search
                </Button>
            </div>

            <div className='col-sm-8'>
                <div style={{textAlign: 'center', display: 'inline-block'}}>
                    <div style={{display: 'inline-block', marginRight: '10px'}}>
                        <h5>Sort: </h5>
                    </div>

                    <Dropdown style={{display: 'inline-block', textAlign: 'center'}}>
                        <Dropdown.Toggle variant="warning" id="dropdown-basic" style={{backgroundColor: 'white', boxShadow: '0 1px 1px 1px rgb(0 0 0 / 8%)'}}>
                            {displayableSort}
                        </Dropdown.Toggle>
                        <Dropdown.Menu style={{textAlign: 'center'}}>
                            {sortOptions.map(sortEl => (
                                <Dropdown.Item key={sortEl.id} onClick={() => handleChangeSortField(sortEl.sortDataField)}>
                                    {sortEl.sortName}
                                </Dropdown.Item>
                            ))}
                            <Dropdown.Divider />
                            <div>
                                <div>
                                    <input type="radio" name="sortType" value="ASC"
                                           onChange={handleChangeSortType}/><span style={{marginLeft: '5px'}}>ASC</span>
                                </div>
                                <div>
                                    <input type="radio" name="sortType" value="DESC" defaultChecked
                                           onChange={handleChangeSortType}/><span style={{marginLeft: '5px'}}>DESC</span>
                                </div>
                            </div>
                        </Dropdown.Menu>
                    </Dropdown>
                </div>

                <div style={{textAlign: 'center', display: 'inline-block', marginLeft: '15px'}}>
                    <div style={{display: 'inline-block', marginRight: '10px'}}>
                        <h5>Filter: </h5>
                    </div>
                    <div style={{display: 'inline-block'}}>
                        <ReactMultiSelectCheckboxes options={filterOptions} onChange={setCheckBoxFilterValues}/>
                    </div>
                </div>
            </div>



        </div>
    )
}

export default AuctionsToolkit;