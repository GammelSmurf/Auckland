import React, {useEffect, useState} from "react";
import {Button} from 'react-bootstrap';
import AuctionService from "../services/AuctionService";
import BootStrapTable from 'react-bootstrap-table-next';
import AuthService from "../services/AuthService";
import ModalDialog from "./ModalDialog";
import LotService from "../services/LotService";
import AuctionsToolkit from "./AuctionsToolkit";

const Auctions = (props) => {
    const [data, setData] = useState([]);
    const [isModalAddActive, setIsModalAddActive] = useState(false);
    const currentUser = AuthService.getCurrentUser();

    const defAucValues = {
        username: currentUser.username,
        name: "Auction name",
        description: "Lorem Ipsum - это текст-\"рыба\", часто используемый в печати и вэб-дизайне. Lorem Ipsum является стандартной \"рыбой\" для текстов на латинице с начала XVI века. В то время некий безымянный печатник создал большую коллекцию размеров и форм шрифтов, используя Lorem Ipsum для распечатки образцов. Lorem Ipsum не только успешно пережил без заметных изменений пять веков, но и перешагнул в электронный дизайн.",
        usersCountLimit: 100,
        beginDateTime: "",
        lotDurationTime: "00:30:00",
        extraTime: "00:00:10",
        status: 'DRAFT'
    }

    const defLotValues = {
        //name: 'Example lot',
        description: 'Lorem Ipsum - это текст-\"рыба\", часто используемый в печати и вэб-дизайне. Lorem Ipsum является стандартной \"рыбой\" для текстов на латинице с начала XVI века. В то время некий безымянный печатник создал большую коллекцию размеров и форм шрифтов, используя Lorem Ipsum для распечатки образцов. Lorem Ipsum не только успешно пережил без заметных изменений пять веков, но и перешагнул в электронный дизайн.',
        minPrice: '100',
        priceIncreaseMinStep: '10',
        pictureLink: 'https://artworld.ru/images/cms/content/catalog3/ivan_shishkin_copy_kamskij_kartina_maslom_pejzazh_utro_v_sosnovom_lesu_1889_is200201_2.jpg'
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

    const parseAuctionFields = (inputData) => {
        let result = []
        inputData.forEach(item => {
            result.push(
                {
                    id: item.id,
                    name: item.name,
                    beginDateTime: parseDateInfo(item.beginDateTime),
                    participants: item.usersCountLimit,
                    likesCount: item.userLikesCount,
                    subscribedUsersCount: item.subscribedUsersCount,
                    status: item.status,
                    creatorUsername: item.creator.username
                }
            )
        });
        return result;
    }

    useEffect(() => {
        /*AuctionService.getAllAuctions().then(
            (response) => {
                setData(parseAuctionFields(response.data.content));
            }
        );*/
    }, []);

    const participantsFormatter = (cell, row) => {
        return (
            <p>{row.subscribedUsersCount + " / " + cell}</p>
        );
    }

    const columns = [{
        dataField: 'name',
        text: 'Auctions name'
    }, {
        dataField: 'creatorUsername',
        text: 'Creator'
    }, {
        dataField: 'beginDateTime',
        text: 'Start'
    }, {
        dataField: 'participants',
        text: 'Participants',
        formatter: participantsFormatter
    }, {
        dataField: 'status',
        text: 'Status'
    }];

    const rowEvents = {
        onClick: (e, row) => {
            props.history.push("/auctions/" + row.id)
        }
    };

    const createAuction = () => {
        defAucValues.beginDateTime = parseMinDate();
        AuctionService.createAuction(defAucValues).then(
            (response) => {
                LotService.createLot({...defLotValues, aucId: response.data.id}).then(
                    () => props.history.push("/auctions/" + response.data.id)
                )
            });
    }

    const handleModalAddClose = () => {setIsModalAddActive(false)};

    return (
        <div className="container">
            <div className="wrapper">
                <div>
                    <div className="toolkit">
                        <Button variant={"warning"} onClick={() => setIsModalAddActive(true)}>
                            Create auction
                        </Button>
                    </div>
                    <AuctionsToolkit setData={(inputData)=>setData(parseAuctionFields(inputData))}/>
                    <BootStrapTable keyField='id'  {...props.baseProps} hover bordered={ false } rowEvents={ rowEvents } data={data} columns={columns}
                                    noDataIndication={() => <p>Table is empty</p>}/>
                </div>
            </div>
            <ModalDialog show={isModalAddActive} hide={handleModalAddClose} title={"New auction"} body={"Do you really want to create new auction? You will be redirected to auction draft..."} action={createAuction}/>
        </div>
    )
}

export default Auctions;