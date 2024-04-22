package roomescape.repository;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;
import roomescape.model.Reservation;

@Repository
public class ReservationRepository {
    private final AtomicLong id = new AtomicLong(1);
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Reservation> actorRowMapper = (resultSet, rowNum) -> {
        return new Reservation(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getDate("date").toLocalDate(),
                resultSet.getTime("time").toLocalTime()
        );
    };

    public ReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ReservationResponse createReservation(ReservationRequest reservationRequest) {
        String sql = "insert into reservation (id, name, date, time) values (?, ?, ?, ?)";
        Reservation reservation = new Reservation(id.getAndIncrement(), reservationRequest.getName(),
                reservationRequest.getDate(), reservationRequest.getTime());
        jdbcTemplate.update(sql, reservation.getId(), reservation.getName(), reservation.getDate(), reservation.getTime());
        return ReservationResponse.of(reservation);
    }

    public List<ReservationResponse> readReservations() {
        String sql = "select id, name, date, time from reservation";
        List<Reservation> reservations = jdbcTemplate.query(sql, actorRowMapper);
        return reservations.stream()
                .map(ReservationResponse::of)
                .toList();
    }

    public boolean deleteReservation(Long id) {
        String sql = "delete from reservation where id = ?";
        return jdbcTemplate.update(sql, id) == id;
    }
}