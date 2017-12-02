package com.koiki.scrooge.event;

import com.koiki.scrooge.scrooge.Scrooge;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@ToString
@Component
public class TransferAmountCalc {
	TransferAmount calculate(List<Scrooge> scrooges) {

		BigDecimal totalAmount = scrooges.stream()
				.map(Scrooge::getPaidAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		Map<String, BigDecimal> paidAmountsPerMember = scrooges
				.stream()
				.collect(Collectors.groupingBy(
						Scrooge::getMemberName,
						Collectors.reducing(BigDecimal.ZERO,
								Scrooge::getPaidAmount,
								BigDecimal::add)));

		BigDecimal averageAmount = totalAmount.divide(BigDecimal.valueOf(paidAmountsPerMember.size()), 0, BigDecimal.ROUND_HALF_UP);

		Map<String, BigDecimal> payableAmountsPerMember = paidAmountsPerMember
				.entrySet()
				.stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().subtract(averageAmount)));

		calculate(payableAmountsPerMember, new ArrayList<TransferAmount>());

		return new TransferAmount();
	}

	private void calculate(
			Map<String, BigDecimal> payableAmountsPerMember,
			List<TransferAmount> transferAmounts) {
		if (payableAmountsPerMember.size() > 1) {
			String from = payableAmountsPerMember
					.entrySet()
					.stream()
					.min((a,b) -> a.getValue().subtract(b.getValue()).compareTo(BigDecimal.ZERO))
					.get()
					.getKey();

			String to = payableAmountsPerMember
					.entrySet()
					.stream()
					.min((a,b) -> b.getValue().subtract(a.getValue()).compareTo(BigDecimal.ZERO))
					.get()
					.getKey();
			log.info("from: {} -> to: {}, amount: {}",
					from,to,payableAmountsPerMember.get(from).multiply(BigDecimal.valueOf(-1)));

			payableAmountsPerMember.put(to, payableAmountsPerMember.get(to).add(payableAmountsPerMember.get(from)));
			payableAmountsPerMember.remove(from);
			calculate(payableAmountsPerMember, transferAmounts);

		} else {

		}
	}
}
