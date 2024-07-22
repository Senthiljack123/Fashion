package com.example.Final.Service;

import com.example.Final.Repo.WishlistItemRepository;
import com.example.Final.model.WishlistItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WishlistItemService {
    @Autowired
    private WishlistItemRepository wishlistItemRepository;

    public List<WishlistItem> getWishlistItemsByUserId(Long userId) {
        return wishlistItemRepository.findByUserId(userId);
    }

    public WishlistItem addWishlistItem(WishlistItem wishlistItem) {
        return wishlistItemRepository.save(wishlistItem);
    }

    public void removeWishlistItem(Long itemId) {
        wishlistItemRepository.deleteById(itemId);
    }
}
