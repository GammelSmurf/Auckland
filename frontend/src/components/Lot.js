import {Button} from "react-bootstrap";
import React from "react";


const Lot = (props) => {
    const errorPictureURL = 'https://st3.depositphotos.com/23594922/31822/v/600/depositphotos_318221368-stock-illustration-missing-picture-page-for-website.jpg';

    return (
        <div className="auctionBlock mt-4" style={{height: "300px", overflowY: 'auto'}}>
            <div style={{width: "30%", display: "inline-block"}}>
                <img alt="No image" src={props.lot.pictureLink ? props.lot.pictureLink : errorPictureURL}
                     style={{width: "100%", maxHeight: "260px", objectFit: "cover"}}
                     onError={(e)=>e.target.src=errorPictureURL}/>
            </div>

            <div style={{width: "70%", float: "right", padding: "20px"}}>
                <div style={{paddingBottom: '20px'}}>
                    <a href={"/auctions/" + props.lot.auctionId} style={{fontSize: '18px'}}>Go to auction</a>
                </div>
                <h5>{props.lot.name}</h5>
                <p>{props.lot.description}</p>
                {!props.isConfirmed &&
                <div style={{textAlign: "right"}}>
                    {props.isWaiting ? <h5 className='basicAnim'>Waiting for confirmation...</h5> :
                        <Button variant="warning" style={{marginRight: "10px"}}
                                onClick={() => props.action(props.lot.id)}>Confirm</Button>}
                </div>
                }

            </div>
        </div>
    )
}

export default Lot;