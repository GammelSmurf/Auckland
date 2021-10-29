import React, {useEffect, useState} from "react";
import {Button} from 'react-bootstrap';
import AuctionService from "../services/AuctionService";
import BootStrapTable from 'react-bootstrap-table-next';
import ToolkitProvider,{Search} from 'react-bootstrap-table2-toolkit';
import AuthService from "../services/AuthService";
import NewAuctionModal from "./NewAuctionModal";

const Auctions = () => {
    const minDateTime = new Date();
    minDateTime.setHours(minDateTime.getHours()+27);
    const [data, setData] = useState([]);
    const [isModalAddActive, setIsModalAddActive] = useState(false);
    const {SearchBar} = Search;
    const currentUser = AuthService.getCurrentUser();


    const parseDate = (itemDateTime) => {
        const dateTime = new Date(itemDateTime);
        const options = { year: '2-digit', month: '2-digit', day: '2-digit',
            hour: '2-digit', minute: '2-digit', second: '2-digit'};
        const dateTimeFormat = new Intl.DateTimeFormat('ru-RU', options).format;
        return dateTimeFormat(dateTime);
    }

    useEffect(() => {
        AuctionService.getAllAuctions().then(
            (response) => {
                let dataPrev = [];
                console.log(response)
                response.data.forEach(item => {
                    dataPrev.push(
                        {
                            id: item.id, name: item.name, beginDate: parseDate(item.beginDate), participants: item.usersLimit, likesCount: item.userLikes, usersCount: item.usersCount
                        }
                    )
                })
                setData(dataPrev);
            }
        );
    }, []);

    const participantsFormatter = (cell, row) => {
        return (
            <p>{row.usersCount + " / " + cell}</p>
        );
    }

    const columns = [{
        dataField: 'id',
        text: 'id',
        sort: true
    },{
        dataField: 'name',
        text: 'Auctions name',
        sort: true
    }, {
        dataField: 'beginDate',
        text: 'Start',
        sort: true
    }, {
        dataField: 'participants',
        text: 'Participants',
        formatter: participantsFormatter,
        sort: true
    }, {
        dataField: 'likesCount',
        text: 'Likes',
        sort: true
    }];

    const createAuction = () => {
        /*AuctionService.createAuction({name: "name", beginDate: "2021-10-23T18:28:48.815Z",
            lotDuration: 10, boostTime: "2021-10-23T18:28:48.815Z", usersLimit: 15, userId: currentUser.id}).then(() => window.location.reload())*/
        setIsModalAddActive(true);
    }

    const handleModalAddClose = () => {setIsModalAddActive(false)};

    return (
        <div className="container">
            <div className="wrapper">
                <ToolkitProvider
                    keyField="id"
                    data={ data }
                    columns={ columns }
                    search
                >
                    {
                        props =>
                            <div>
                                <div className="toolkit">
                                    <SearchBar { ...props.searchProps } srText=""/>
                                    <Button variant={"warning"} onClick={createAuction} style={{marginBottom: "4px"}}>
                                        Create auction
                                    </Button>
                                </div>
                                <BootStrapTable keyField='id'  {...props.baseProps} hover bordered={ false }
                                                noDataIndication={() => <p>Table is empty</p>}/>
                            </div>

                    }
                </ToolkitProvider>
                <NewAuctionModal show={isModalAddActive} hide={handleModalAddClose} minDateTime={minDateTime}/>
            </div>

        </div>
    )
}

export default Auctions;