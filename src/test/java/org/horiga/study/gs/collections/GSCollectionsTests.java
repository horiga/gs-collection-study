package org.horiga.study.gs.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import com.google.common.base.Joiner;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.list.mutable.ListAdapter;
import com.gs.collections.impl.partition.list.PartitionFastList;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Builder;

public class GSCollectionsTests
{

	@Test
	public void selectAndRejectPatterns() {
		List<Integer> numbers = new ArrayList<Integer>();
		IntStream.range(0, 100).forEach(n -> {
			numbers.add(RandomUtils.nextInt(0, 100));
		});
		MutableList<Integer> selectedGreaterThan70 = ListAdapter.adapt(numbers).select(Predicates.greaterThan(70));
		System.out.println("selected > 70 : " + Joiner.on(",").join(selectedGreaterThan70));

		MutableList<Integer> rejectedGreaterThan70 = ListAdapter.adapt(numbers).reject(Predicates.greaterThan(70));
		System.out.println("rejected > 70 : " + Joiner.on(",").join(rejectedGreaterThan70));
	}

	@Test
	public void selectAndRejectPatternsWithPartition() {
		PartitionFastList<Profile> partitionByActives = newProfiles(50).partition(p -> {
			return p.isActive();
		});
		System.out.println("selected: " + Joiner.on(",").join(partitionByActives.getSelected()));
		System.out.println("rejected: " + Joiner.on(",").join(partitionByActives.getRejected()));
	}

	@SuppressWarnings("serial")
	@Test
	public void collectPatterns() {
		final FastList<Profile> profiles = newProfiles(10);

		final Predicate<Profile> actives = p -> {
			return p.active;
		};

		final Function<Profile, String> displayNamesFunc = new Function<Profile, String>() {
			public String valueOf(Profile p) {
				return p.displayName;
			}
		};

		p("select/collect",
			profiles
				.select(actives)
				.collect(displayNamesFunc));

		// collectIf
		p("collectIf",
			profiles.collectIf(actives, displayNamesFunc));
	}

	@SuppressWarnings("serial")
	@Test
	public void collectWith() {
		Function2<Profile, String, Profile> addPrefixOnMemberIdFunc =
				new Function2<Profile, String, Profile>()
				{
					public Profile value(final Profile p, final String prefix)
					{
						p.mid = prefix + p.mid;
						return p;
					}
				};
		FastList<Profile> collectWith = newProfiles(10).collectWith(
			addPrefixOnMemberIdFunc, "hoge@");

		p("collectWith", collectWith.collect(new Function<Profile, String>() {
			@Override
			public String valueOf(Profile p) {
				return p.mid;
			}
		}));
	}

	private static void p(String label, Collection<?> values) {
		System.out.println(label + ":" + Joiner.on(",").join(values));
	}

	@Builder
	@Getter
	@ToString(callSuper = false, includeFieldNames = true)
	public static class Profile {
		String mid;
		String displayName;
		boolean active;
	}

	private static FastList<Profile> newProfiles(int num) {
		FastList<Profile> friends = FastList.newList();
		IntStream.range(0, num).forEach(i -> {
			friends.add(Profile.builder()
				.active(RandomUtils.nextInt(0, 10) % 2 == 0)
				.mid(UUID.randomUUID().toString())
				.displayName(String.format("player%08d", i))
				.build()
				);
		});
		System.out.println(
			Joiner.on("\n").join(friends)
			);
		return friends;
	}

}
