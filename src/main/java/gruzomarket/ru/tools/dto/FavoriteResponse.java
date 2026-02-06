package gruzomarket.ru.tools.dto;

import lombok.Data;

@Data
public class FavoriteResponse {
    private Long productId;
    private boolean isFavorite;
    private long favoriteCount;
}