package com.example.security.service;

import com.example.security.model.Favorite;
import com.example.security.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {
    @Autowired
    private FavoriteRepository favoriteRepository;

    public String addFavorite(Long userId, Long itemId){
        if(!favoriteRepository.exists(userId, itemId)){
            return favoriteRepository.addFavorite(userId, itemId);
        }
        return "The item is already in the favorite list";
    }

    public List<Favorite> getFavoritesByUserId(Long userId){
        return favoriteRepository.findByUserId(userId);
    }

    public String removeFavorite(Long userId, Long itemId){
        return favoriteRepository.removeFavorite(userId,itemId);
    }

    public  boolean isFavorite(Long userId, Long itemId){
        return favoriteRepository.exists(userId, itemId);
    }

    public String removeAllFavoriteByUserId(Long userId){
        return favoriteRepository.deleteByUserId(userId);
    }
}