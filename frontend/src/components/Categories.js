import React, {useEffect, useRef, useState} from 'react';
import {Button, FormControl, InputGroup} from "react-bootstrap";
import CategoryService from "../services/CategoryService";
import {Dropdown} from "react-bootstrap";
import TagService from "../services/TagService";
import AuctionService from "../services/AuctionService";

const Categories = (props) => {
    const [allCategories, setAllCategories] = useState([]);
    const [aucCategories, setAucCategories] = useState([]);
    const [aucTags, setAucTags] = useState([]);
    const [isAddingTag, setIsAddingTag] = useState(false);
    const [newTagName, setNewTagName] = useState('');
    const [errorMessage, setErrorMessage] = useState('');


    const colors = [
        '#FF9E00', '#FF4545', '#40E687','#00C1FF','#AD61FF'
    ]

    const getColor = (catId) => {
        return colors[catId%colors.length];
    }

    useEffect(() => {
        setAucCategories(props.auction.categories);
        setAucTags(props.auction.tags);
        CategoryService.getAllCategories().then((response)=>{
            setAllCategories(response.data);
        });
    }, [])

    const handleAddCategory = (categoryId) => {
        AuctionService.addCategory({auctionId: props.auction.id, categoryId: categoryId})
            .then((response)=>setAucCategories(aucCategories.concat(response.data)))
            .catch((error)=>{setErrorMessage(null);setErrorMessage(error.response.data.message)})
    }

    const handleDeleteTag = (tagId) => {
        TagService.deleteTag(tagId).then(()=>setAucTags(aucTags.filter(tag=>tag.id !== tagId)));
    }

    const handleDeleteCategory = (categoryId) => {
        AuctionService.removeCategory({auctionId: props.auction.id, categoryId: categoryId})
            .then(()=>{
                setAucCategories(aucCategories.filter(cat=>cat.id !== categoryId));
                setAucTags(aucTags.filter(tag=>tag.categoryId !== categoryId));
            });
    }

    const handleNewTagKeyDown  = (e, catId) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            setIsAddingTag(false);
            TagService.addTag({name: newTagName, categoryId: catId, auctionId: props.auction.id})
                .then((response)=>setAucTags(aucTags.concat(response.data)))
                .catch(()=>{setErrorMessage(null);setErrorMessage('The tag should contain at least 3 letters')});
        }
    }

    return(
        <div className='basicAnim' style={{textAlign: 'center'}}>
            <div style={{height: '125px'}}>
                <h5>Categories and tags</h5>
                <hr size={3} style={{color: '#9434B3'}}/>
                <Dropdown style={{display: 'inline-block', marginRight: '15px'}}>
                    <Dropdown.Toggle variant="warning" id="dropdown-basic">
                        Add category
                    </Dropdown.Toggle>

                    <Dropdown.Menu style={{textAlign: 'center'}}>
                        {allCategories.map(cat =>
                            <Dropdown.Item key={cat.id} href="#/action-1" onClick={()=>handleAddCategory(cat.id)}>
                                <span className='tag'>{cat.name}</span>
                            </Dropdown.Item>
                        )}
                    </Dropdown.Menu>
                </Dropdown>
                <Button variant='warning' onClick={()=>setIsAddingTag(!isAddingTag)}>Add tags</Button>
                {errorMessage &&
                <p className='text-danger responseText'>{errorMessage}</p>}
            </div>

            {aucCategories && aucCategories.map(cat=>
                <div key={cat.id} className='auctionBlock' style={{marginTop: '15px', backgroundColor: '#E6EBEE'}}>
                    <div>
                        <span className='tag' style={{color: getColor(cat.id)}}
                              onClick={()=>handleDeleteCategory(cat.id)}><b>{cat.name}</b></span>
                    </div>
                    <div>
                        {aucTags && aucTags.filter(tag=>tag.categoryId === cat.id).map(tag=>
                            <div key={tag.id} style={{display: 'inline-block', marginRight: '12px'}}>
                                <p><span style={{borderBottom: '2px solid '+getColor(cat.id), cursor: 'not-allowed'}}
                                         onClick={()=>handleDeleteTag(tag.id)}>{tag.name}</span></p>
                            </div>
                        )}
                        {isAddingTag &&
                            <InputGroup style={{display: 'inline-block', width: '100px', height: '25px'}}>
                                <FormControl style={{width: '100%', height: '100%'}}
                                             onChange={(e)=>setNewTagName(e.target.value)}
                                             onKeyDown={(e)=>handleNewTagKeyDown(e,cat.id)}
                                             aria-label="New tag"
                                             placeholder="New tag"/>
                            </InputGroup>
                        }

                    </div>
                </div>
            )}


        </div>
    )
}

export default Categories;