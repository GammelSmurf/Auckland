import BootStrapTable from "react-bootstrap-table-next";
import React, {useEffect, useState} from "react";
import UserService from "../services/UserService";
import AuthService from "../services/AuthService";
import {Button, Modal} from "react-bootstrap";
import ModalGiveCash from "./ModalGiveCash";
import ModalDialog from "./ModalDialog";


const Users = () => {
    const [data,setData] = useState([]);
    const [isModalGiveCash, setIsModalGiveCash] = useState(false);
    const [isModalBanUser, setIsModalBanUser] = useState(false);
    const [isModalUnBanUser, setIsModalUnBanUser] = useState(false);
    const [activeUsername, setActiveUsername] = useState('');
    const currentUser = AuthService.getCurrentUser();

    useEffect(() => {
        UserService.getUsers().then(
            (response) => {
                let dataPrev = [];
                response.data.forEach(item => {
                    dataPrev.push(
                        item
                    )
                })
                setData(dataPrev);
            }
        );
    }, []);

    const columns = [{
        dataField: 'id',
        text: 'id',
        sort: true
    },{
        dataField: 'username',
        text: 'Username',
        sort: true
    }, {
        dataField: 'email',
        text: 'Email',
        sort: true
    }, {
        dataField: 'currency',
        text: 'Cash',
        sort: true
    }, {
        dataField: 'isBanned',
        text: 'Banned',
        sort: true
    }, {
        dataField: 'enabled',
        text: 'Enabled',
        sort: true
    }];

    const rowStyle = (row, rowIndex) => {
        const style = {};
        if (row.id === currentUser.id) {
            style.backgroundColor = 'rgba(255,193,7, 0.3)';
        }
        return style;
    };

    const handleBanUser = () => {
        UserService.banUser(activeUsername).then(()=>window.location.reload());
    }

    const handleUnBanUser = () => {
        UserService.unbanUser(activeUsername).then(()=>window.location.reload());
    }

    const expandRow = {
        onlyOneExpanding: true,
        renderer: row => (
            <div>
                <div style={{textAlign: 'right'}}>
                    <Button variant="warning" style={{marginRight: '10px'}} onClick={()=> {setActiveUsername(row.username);setIsModalGiveCash(true);}}>Give cash</Button>
                    {(currentUser.id !== row.id && !row.isBanned) &&
                        <Button variant="danger" onClick={()=>{setActiveUsername(row.username);setIsModalBanUser(true);}}>Ban</Button>}
                    {(currentUser.id !== row.id && row.isBanned) &&
                    <Button variant="danger" onClick={()=>{setActiveUsername(row.username);setIsModalUnBanUser(true);}}>Unban</Button>}
                </div>
            </div>
        )
    };

    return(
        <div className="container">
            <div className="wrapper">
                <BootStrapTable keyField='id' hover bordered={ false } columns={columns} data={data} rowStyle={rowStyle} expandRow={expandRow}//rowEvents={ rowEvents }
                                noDataIndication={() => <p>Table is empty</p>}/>
            </div>
            <ModalGiveCash show={isModalGiveCash} hide={() => {setIsModalGiveCash(false);window.location.reload()}} username={activeUsername}/>
            <ModalDialog show={isModalBanUser} hide={()=>setIsModalBanUser(false)} title={'Ban user'} body={'Do you really want to ban user ' + activeUsername + ' ?'} action={handleBanUser}/>
            <ModalDialog show={isModalUnBanUser} hide={()=>setIsModalUnBanUser(false)} title={'Unban user'} body={'Do you really want to unban user ' + activeUsername + ' ?'} action={handleUnBanUser}/>
        </div>
    )
}

export default Users;