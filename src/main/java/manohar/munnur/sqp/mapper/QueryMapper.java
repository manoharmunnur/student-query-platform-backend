package manohar.munnur.sqp.mapper;

import manohar.munnur.sqp.dto.QueryDto;
import manohar.munnur.sqp.entity.queries.Priority;
import manohar.munnur.sqp.entity.queries.Query;
import manohar.munnur.sqp.entity.queries.QueryStatus;

public class QueryMapper {

    public static QueryDto mapToQueryDto(Query q) {
        QueryDto dto = new QueryDto();
        dto.setId(q.getId());
        dto.setCreatedByUserId(q.getCreatedByUserId());
        dto.setCreatedByRole(q.getCreatedByRole());
        dto.setTitle(q.getTitle());
        dto.setDescription(q.getDescription());
        dto.setPriority(q.getPriority().name());
        dto.setStatus(q.getStatus().name());
        dto.setResponse(q.getResponse());
        dto.setCreatedAt(q.getCreatedAt());
        dto.setUpdatedAt(q.getUpdatedAt());
        dto.setResolvedAt(q.getResolvedAt());
        return dto;
    }

    // DTO -> Entity (useful for update operations)
    public static Query mapToQuery(QueryDto dto) {
        if (dto == null) {
            return null;
        }

        Query query = new Query();
        query.setId(dto.getId());  // keep id for updates; null for create
        query.setCreatedByUserId(dto.getCreatedByUserId());
        query.setCreatedByRole(dto.getCreatedByRole());
        query.setTitle(dto.getTitle());
        query.setDescription(dto.getDescription());

        if (dto.getPriority() != null) {
            query.setPriority(Priority.valueOf(dto.getPriority()));
        }

        if (dto.getStatus() != null) {
            query.setStatus(QueryStatus.valueOf(dto.getStatus()));
        }

        query.setResponse(dto.getResponse());
        query.setCreatedAt(dto.getCreatedAt());
        query.setUpdatedAt(dto.getUpdatedAt());
        query.setResolvedAt(dto.getResolvedAt());

        // answers and attachment are intentionally NOT mapped here
        return query;
    }
}
