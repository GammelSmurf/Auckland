import React, {useEffect, useState} from "react";
import {Button} from 'react-bootstrap';
import AuctionService from "../services/AuctionService";
import BootStrapTable from 'react-bootstrap-table-next';
import ToolkitProvider,{Search} from 'react-bootstrap-table2-toolkit';
import AuthService from "../services/AuthService";
import ModalDialog from "./ModalDialog";

const Auctions = (props) => {
    const [data, setData] = useState([]);
    const [isModalAddActive, setIsModalAddActive] = useState(false);
    const {SearchBar} = Search;
    const currentUser = AuthService.getCurrentUser();

    const values = {
        username: currentUser.username,
        aucName: "Auction name",
        aucDescription: "Lorem Ipsum - это текст-\"рыба\", часто используемый в печати и вэб-дизайне. Lorem Ipsum является стандартной \"рыбой\" для текстов на латинице с начала XVI века. В то время некий безымянный печатник создал большую коллекцию размеров и форм шрифтов, используя Lorem Ipsum для распечатки образцов. Lorem Ipsum не только успешно пережил без заметных изменений пять веков, но и перешагнул в электронный дизайн.",
        usersLimit: 100,
        beginDate: "",
        lotDuration: "00:30:00",
        boostTime: "00:00:10",
        status: 'DRAFT'
    }

    const parseMinDate = () => {
        const minDate = new Date();
        minDate.setHours(minDate.getHours()+27);
        return minDate.toISOString().slice(0,-5).split('T').join(' ');
    }

    const parseDateInfo = (itemDateTime) => {
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
                response.data.forEach(item => {
                    dataPrev.push(
                        {
                            id: item.id, name: item.name, beginDate: parseDateInfo(item.beginDate), participants: item.usersLimit, likesCount: item.userLikes, usersCount: item.usersCount, status: item.status
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
    }, {
        dataField: 'status',
        text: 'Status',
        sort: true
    }];

    const rowEvents = {
        onClick: (e, row, rowIndex) => {
            props.history.push("/auctions/" + row.id)
        }
    };

    const createAuction = () => {
        values.beginDate = parseMinDate();
        AuctionService.createAuction(values).then((response) => props.history.push("/auctions/" + response.data.id));
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
                                    <Button variant={"warning"} onClick={() => setIsModalAddActive(true)} style={{marginBottom: "4px"}}>
                                        Create auction
                                    </Button>
                                </div>
                                <BootStrapTable keyField='id'  {...props.baseProps} hover bordered={ false } rowEvents={ rowEvents }
                                                noDataIndication={() => <p>Table is empty</p>}/>
                            </div>

                    }
                </ToolkitProvider>
            </div>
            <ModalDialog show={isModalAddActive} hide={handleModalAddClose} title={"New auction"} body={"Do you really want to create new auction? You will be redirected to auction draft..."} action={createAuction}/>
        </div>
    )
}

export default Auctions;